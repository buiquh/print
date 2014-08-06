package com.eastbay.performancezone.print.content;

import com.eastbay.performancezone.print.Request.PrintRequest;
import com.eastbay.performancezone.print.Request.Product;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: bob.gause
 * Date: 5/23/14
 * Time: 2:09 PM
 * SDG
 */
public class PickListTest {
    @Test
    public void testRender() throws Exception {
        String jsonInput = "{\"products\": [\n" +
                "    {\n" +
                "        \"barCode\": \"12345\",\n" +
                "        \"brand\": \"Nike\",\n" +
                "        \"color\": \"Black/White/Black\",\n" +
                "        \"description\": \"Athletic Shoes\",\n" +
                "        \"extendedPrice\": 149.99,\n" +
                "        \"imageURL\": \"http://images.eastbay.com/is/image/EBFL/1425011?wid=2000&hei=2000&qlt=60\",\n" +
                "        \"microSite\": true,\n" +
                "        \"name\": \"Vapor Carbon Elite 2014 TD\",\n" +
                "        \"number\": \"31425019\",\n" +
                "        \"price\": 149.99,\n" +
                "        \"quantity\": 1,\n" +
                "        \"size\": \"10.5\"\n" +
                "}]}";
        PrintRequest jsonRequest = PrintRequest.fromJsonToPrintRequest(jsonInput);
        PrintRequest request = new PrintRequest();
        Product[] products = new Product[3];
        products[0] = jsonRequest.getProducts()[0];
        products[1] = jsonRequest.getProducts()[0];
        products[2] = jsonRequest.getProducts()[0];
        request.setProducts(products);
        PickList pickList = new PickList(request);
        pickList.render();
        Assert.assertTrue(true);
    }

    @Test
    public void testCalculatePageCount() throws Exception {
        PickList pickList = new PickList(null);
        Assert.assertEquals(4, pickList.calculatePageCount(7));
        Assert.assertEquals(4, pickList.calculatePageCount(8));
        Assert.assertEquals(1, pickList.calculatePageCount(1));
        Assert.assertEquals(1, pickList.calculatePageCount(2));
        Assert.assertEquals(2, pickList.calculatePageCount(3));
    }
}
