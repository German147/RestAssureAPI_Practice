package CRUD;

import database.DBConnection;
import exceptions.DeleteProductException;
import exceptions.GetProductException;
import exceptions.InsertProductException;
import exceptions.UpdateProductException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductDAO {

    public boolean insertProduct(Product product) {
        String sql = "INSERT INTO products (name, description, price, quantity) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, product.getQuantity());

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            throw new InsertProductException("Product was not inserted");
        }
    }
    public Product getProductById(int id) {
        Product product = null;
        String sql = "SELECT * FROM products WHERE id = ?";

        try (Connection conn = DBConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                product = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                );
            }

        } catch (SQLException e) {
            throw new GetProductException("Failed to read a product by id");
        }
        if (product!=null){
            System.out.println(product.toString());
        }

        return product;
    }
    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET name = ?, description = ?, price = ?, quantity = ? WHERE id = ?";

        try (Connection conn = DBConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, product.getQuantity());
            stmt.setInt(5, product.getId());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            throw new UpdateProductException("Product could not be updated");
        }
    }
    public boolean deleteProductById(int id) {
        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection conn = DBConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsDeleted = stmt.executeUpdate();

            return rowsDeleted > 0;

        } catch (SQLException e) {
            throw new DeleteProductException("Failed to delete product with id: " + id);
        }
    }


}
