package com.ll.demo.model;

import java.math.BigDecimal;
import java.util.List;

public class DepthWrapper {

  private List<Depth> asks;
  private List<Depth> bids;

  public List<Depth> getAsks() {
    return asks;
  }

  public void setAsks(List<Depth> asks) {
    this.asks = asks;
  }

  public List<Depth> getBids() {
    return bids;
  }

  public void setBids(List<Depth> bids) {
    this.bids = bids;
  }

  public static class Depth {
    /**
     * amount : 0.04606
     * price : 3249.49
     */

    private BigDecimal amount;
    private BigDecimal price;

    public BigDecimal getAmount() {
      return amount;
    }

    public void setAmount(BigDecimal amount) {
      this.amount = amount;
    }

    public BigDecimal getPrice() {
      return price;
    }

    public void setPrice(BigDecimal price) {
      this.price = price;
    }
  }
}
