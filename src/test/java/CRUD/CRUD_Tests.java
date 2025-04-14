package CRUD;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CRUD_Tests {
    ProductDAO productDAO = new ProductDAO();

    @Test
    public void insertProduct() {
        ProductDAO productDAO = new ProductDAO();

        Product newProduct = new Product("Monitor", "LED", 150.0, 5);
        boolean isInserted = productDAO.insertProduct(newProduct);

        Assert.assertTrue(isInserted, "The product should be inserted successfully.");

        System.out.println("Inserted Product:");
        System.out.println("Name: " + newProduct.getName());
        System.out.println("Description: " + newProduct.getDescription());
        System.out.println("Price: " + newProduct.getPrice());
        System.out.println("Quantity: " + newProduct.getQuantity());
    }


    @Test
    public void getProductTest() {
        ProductDAO productDAO = new ProductDAO();
        Product product = productDAO.getProductById(2);

        Assert.assertNotNull(product, "Product with ID 2 should not be null");

        System.out.println("Product retrieved:");
        System.out.println("ID: " + product.getId());
        System.out.println("Name: " + product.getName());
        System.out.println("Description: " + product.getDescription());
        System.out.println("Price: " + product.getPrice());
        System.out.println("Quantity: " + product.getQuantity());
    }


    @Test
    public void updateProductTest() {
        ProductDAO dao = new ProductDAO();

        // Assume there's already a product with ID 1
        Product updatedProduct = new Product(1, "Earphones", "Black and Red", 999.99, 10);
        dao.updateProduct(updatedProduct);

//        Assert.assertTrue(isUpdated, "Product should be updated successfully");

        // Optional: retrieve product and validate data
        Product retrievedProduct = dao.getProductById(1);
        Assert.assertEquals(retrievedProduct.getName(), "Earphones");
        Assert.assertEquals(retrievedProduct.getDescription(), "Black and Red");
        Assert.assertEquals(retrievedProduct.getPrice(), 999.99);
        Assert.assertEquals(retrievedProduct.getQuantity(), 10);
    }

    @Test
    public void deleteProductTest(){
        productDAO.deleteProductById(1);
        Product deletedProduct = productDAO.getProductById(1);
        Assert.assertNull(deletedProduct,"Product should not exist");
    }
}
