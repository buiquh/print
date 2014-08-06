package com.eastbay.performancezone.print.Request;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bob.gause
 * Date: 5/18/14
 * Time: 3:07 PM
 * SDG
 */
public class PrintRequest {
    private Product[] products = new Product[0];
    private boolean testSuccess = false;
    private boolean testFail = false;

    public static PrintRequest fromJsonToPrintRequest(String json) {
        PrintRequest intoPrintRequest = null;
        if (json != null) {
            intoPrintRequest = new JSONDeserializer<PrintRequest>().use(null, PrintRequest.class).deserialize(json);
            if (intoPrintRequest != null && intoPrintRequest.getProducts() != null) {
                for (Product product : intoPrintRequest.getProducts()) {
                    if (product.getPrice() != null) {
                        product.setPrice(product.getPrice().setScale(2, BigDecimal.ROUND_HALF_EVEN));
                    }
                    if (product.getExtendedPrice() != null) {
                        product.setExtendedPrice(product.getExtendedPrice().setScale(2, BigDecimal.ROUND_HALF_EVEN));
                    }
                }
            }
        }
        return intoPrintRequest;
    }

    public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }

    public String toJson() {
        return new JSONSerializer().include("products").exclude("*.class").serialize(this);
    }

    public static Collection<PrintRequest> fromJsonArrayToPrintRequests(String json) {
        return new JSONDeserializer<List<PrintRequest>>().use(null, ArrayList.class).use("values", PrintRequest.class).deserialize(json);
    }

    public Product[] getProducts() {
        return products;
    }

    public void setProducts(Product[] products) {
        this.products = products;
    }

    public boolean isTestSuccess() {
        return testSuccess;
    }

    public void setTestSuccess(boolean testSuccess) {
        this.testSuccess = testSuccess;
    }

    public boolean isTestFail() {
        return testFail;
    }

    public void setTestFail(boolean testFail) {
        this.testFail = testFail;
    }
}
