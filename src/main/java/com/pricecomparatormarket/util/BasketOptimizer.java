package com.pricecomparatormarket.util;

import com.pricecomparatormarket.dto.request.BasketOptimizeRequest;
import com.pricecomparatormarket.dto.response.SuggestedListDto;
import com.pricecomparatormarket.exception.NoPriceException;
import com.pricecomparatormarket.model.Discount;
import com.pricecomparatormarket.model.PriceSnapshot;
import com.pricecomparatormarket.model.Product;
import com.pricecomparatormarket.model.Store;
import com.pricecomparatormarket.repository.DiscountRepository;
import com.pricecomparatormarket.repository.PriceSnapshotRepository;
import com.pricecomparatormarket.repository.ProductRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BasketOptimizer {

  private final PriceSnapshotRepository snapshotRepo;
  private final DiscountRepository discountRepo;
  private final ProductRepository productRepo;

  public List<SuggestedListDto> optimise(
      List<BasketOptimizeRequest.Item> reqItems, int maxStores, LocalDate today) {

    /* ---- Build the cheapest offer per product/store ---- */
    record Offer(Store store, BigDecimal unitPrice) {}

    Map<String, List<Offer>> offersPerProduct = new HashMap<>();

    for (var it : reqItems) {
      String pid = it.productId();

      List<PriceSnapshot> snaps = snapshotRepo.findTodayByProduct(pid, today);

      Map<Long, BigDecimal> discByStore =
          discountRepo.findActiveForProduct(pid, today).stream()
              .collect(
                  Collectors.toMap(d -> d.getStore().getId(), Discount::getPercentageOfDiscount));

      List<Offer> offers = new ArrayList<>();
      for (PriceSnapshot ps : snaps) {
        BigDecimal shelf = ps.getPrice();
        BigDecimal pct = discByStore.getOrDefault(ps.getStore().getId(), BigDecimal.ZERO);
        BigDecimal eff =
            shelf.multiply(
                BigDecimal.ONE.subtract(
                    pct.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)));
        offers.add(new Offer(ps.getStore(), eff));
      }
      offersPerProduct.put(pid, offers);
    }

    /* ---- Greedy pick cheapest per product ---- */
    Map<Store, List<SuggestedListDto.Item>> bucket = new HashMap<>();

    for (var it : reqItems) {
      Product p = productRepo.getReferenceById(it.productId());
      Offer best =
          offersPerProduct.get(it.productId()).stream()
              .min(Comparator.comparing(o -> o.unitPrice))
              .orElseThrow(() -> new NoPriceException("No price for " + it.productId()));

      BigDecimal line = best.unitPrice.multiply(it.quantity());

      bucket
          .computeIfAbsent(best.store, __ -> new ArrayList<>())
          .add(
              new SuggestedListDto.Item(
                  p.getProductId(), p.getProductName(), it.quantity(), best.unitPrice, line));
    }

    /* ---- Trim to maxStores (drop most-expensive buckets) ---- */
    List<Map.Entry<Store, List<SuggestedListDto.Item>>> entries =
        new ArrayList<>(bucket.entrySet());

    entries.sort(
        Comparator.comparing(
            e ->
                e.getValue().stream()
                    .map(SuggestedListDto.Item::linePrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)));

    while (entries.size() > maxStores) {
      entries.removeLast();
    }

    /* ---- DTO ---- */
    return entries.stream()
        .map(
            e ->
                new SuggestedListDto(
                    e.getKey().getName(),
                    e.getValue().stream()
                        .map(SuggestedListDto.Item::linePrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add),
                    e.getValue()))
        .sorted(Comparator.comparing(SuggestedListDto::subTotal))
        .toList();
  }
}
