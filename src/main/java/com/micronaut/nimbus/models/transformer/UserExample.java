package com.micronaut.nimbus.models.transformer;


import com.micronaut.nimbus.annotations.JsonCleanPrefix;
import com.micronaut.nimbus.annotations.JsonNestedTransform;
import com.micronaut.nimbus.annotations.JsonToLower;
import com.micronaut.nimbus.annotations.JsonToUpper;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class UserExample {

    @JsonCleanPrefix(prefix = "user_")
    private String userId;

    @JsonToUpper
    private String name;

    @JsonToLower
    private String email;

    @JsonNestedTransform(path = "address.home")
    private AddressDto homeAddress;

    @JsonNestedTransform(path = "address.office")
    private AddressDto officeAddress;

    private OrderDto orders;

    // Getters and setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AddressDto getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(AddressDto homeAddress) {
        this.homeAddress = homeAddress;
    }

    public AddressDto getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(AddressDto officeAddress) {
        this.officeAddress = officeAddress;
    }

    public OrderDto getOrders() {
        return orders;
    }

    public void setOrders(OrderDto orders) {
        this.orders = orders;
    }

    @Serdeable
    public static class AddressDto {
        private String street;
        private String city;
        private String zipCode;

        // Getters and setters

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }
    }

    @Serdeable
    public static class OrderDto {

        @JsonNestedTransform(path = "test.order.ID")
        private String orderId;

        @JsonNestedTransform(path = "items")
        private ItemDto items;

        private double totalAmount;

        // Getters and setters

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public ItemDto getItems() {
            return items;
        }

        public void setItems(ItemDto items) {
            this.items = items;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
        }
    }

    @Serdeable
    public static class ItemDto {
        private String name;
        private int quantity;
        private double price;

        // Getters and setters

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }
}
