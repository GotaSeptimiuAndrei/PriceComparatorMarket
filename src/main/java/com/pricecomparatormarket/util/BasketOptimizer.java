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

/**
 * Splits a basket of products across stores to minimize total cost,
 * then, if more than maxStores, greedily drops the store whose removal
 * increases the total by the least amount.
 */
@Component
@RequiredArgsConstructor
public class BasketOptimizer {

  private final PriceSnapshotRepository snapshotRepo;
  private final DiscountRepository discountRepo;
  private final ProductRepository productRepo;

  public List<SuggestedListDto> optimise(
      List<BasketOptimizeRequest.Item> reqItems, int maxStores, LocalDate today) {

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
            shelf
                .multiply(
                    BigDecimal.ONE.subtract(
                        pct.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)))
                .setScale(2, RoundingMode.HALF_UP); // 2-decimal

        offers.add(new Offer(ps.getStore(), eff));
      }
      offersPerProduct.put(pid, offers);
    }

    Map<Store, List<SuggestedListDto.Item>> bucket = new HashMap<>();
    Map<String, Offer> chosen = new HashMap<>();

    for (var it : reqItems) {
      Product p = productRepo.getReferenceById(it.productId());
      Offer best =
          offersPerProduct.get(it.productId()).stream()
              .min(Comparator.comparing(o -> o.unitPrice))
              .orElseThrow(() -> new NoPriceException("No price for " + it.productId()));

      chosen.put(it.productId(), best);

      BigDecimal line = best.unitPrice.multiply(it.quantity()).setScale(2, RoundingMode.HALF_UP);

      bucket
          .computeIfAbsent(best.store, __ -> new ArrayList<>())
          .add(
              new SuggestedListDto.Item(
                  p.getProductId(), p.getProductName(), it.quantity(), best.unitPrice, line));
    }

    while (bucket.size() > maxStores) {

      Store storeToDrop = null;
      BigDecimal minPenalty = BigDecimal.valueOf(Long.MAX_VALUE);

      for (Store candidate : bucket.keySet()) {

        BigDecimal penalty = BigDecimal.ZERO;

        for (SuggestedListDto.Item item : bucket.get(candidate)) {
          Offer alt =
              offersPerProduct.get(item.productId()).stream()
                  .filter(o -> !o.store.equals(candidate))
                  .min(Comparator.comparing(o -> o.unitPrice))
                  .orElse(null);
          if (alt == null) {
            penalty = null;
            break;
          } // cannot drop
          BigDecimal altLine =
              alt.unitPrice.multiply(item.quantity()).setScale(2, RoundingMode.HALF_UP);
          penalty = penalty.add(altLine.subtract(item.linePrice()));
        }
        if (penalty != null && penalty.compareTo(minPenalty) < 0) {
          minPenalty = penalty;
          storeToDrop = candidate;
        }
      }

      List<SuggestedListDto.Item> itemsToMove = bucket.remove(storeToDrop);
      for (SuggestedListDto.Item item : itemsToMove) {

        Store finalStoreToDrop = storeToDrop;
        Offer alt =
            offersPerProduct.get(item.productId()).stream()
                .filter(o -> !o.store.equals(finalStoreToDrop))
                .min(Comparator.comparing(o -> o.unitPrice))
                .orElseThrow();

        BigDecimal newLine =
            alt.unitPrice.multiply(item.quantity()).setScale(2, RoundingMode.HALF_UP);

        bucket
            .computeIfAbsent(alt.store, __ -> new ArrayList<>())
            .add(
                new SuggestedListDto.Item(
                    item.productId(), item.productName(), item.quantity(), alt.unitPrice, newLine));
      }
    }
    return bucket.entrySet().stream()
        .map(
            e ->
                new SuggestedListDto(
                    e.getKey().getName(),
                    e.getValue().stream()
                        .map(SuggestedListDto.Item::linePrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .setScale(2, RoundingMode.HALF_UP),
                    e.getValue()))
        .sorted(Comparator.comparing(SuggestedListDto::subTotal))
        .toList();
  }
}
