package org.cgi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.cgi.models.Address;
import org.cgi.models.Device;
import org.cgi.models.Order;
import org.cgi.models.PaymentMethod;
import org.cgi.models.Shop;
import org.cgi.models.SmartOrder;
import org.jboss.resteasy.reactive.ResponseStatus;

@Path("/api")
public class IoPResource {

    List<Device> devices = new ArrayList<>();
    List<Shop> shops = new ArrayList<>();
    List<Address> addresses = new ArrayList<>();
    List<PaymentMethod> paymentMethods = new ArrayList<>();
    List<Order> orders = new ArrayList<>();

    public static Address address1;
    public static Address address2;
    public static Address address3;

    IoPResource() {
        // Devices
        Device coffeeMachine = new Device("4d490d79-5807-4a91-a3cd-64086d39411f", "HA_ID", "Coffee Machine",
                "KEURIG K-Supreme Plus SMART", "Coffee Maker", null);
        Device printer = new Device("65493ee9-24a8-425a-a89e-e5ac9ef02407", "HA_ID", "Printer", "HP DeskJet 4120e",
                "Printer", null);

        // Payment Methods
        PaymentMethod debitPayment = new PaymentMethod("Debit Card", "DE12 23456 78910");
        PaymentMethod giroPayment = new PaymentMethod("Giro Account", "DE12 23456 78910");
        PaymentMethod paypalPayment = new PaymentMethod("Paypal", "max@mustermann.com");

        // Addresses
        address1 = new Address("318562f8-596e-4941-b32d-42ab3bba26f5", "Max", "Mustermann", "Firma Str.", 20, 54989,
                "Wiesbaden", false);
        address2 = new Address("79019e05-44af-4849-beb2-2ae06f9dd8d7", "John", "Doe", "Weizen Str.", 3, 54989,
                "Wiesbaden", false);
        address3 = new Address("3e76dba9-46e5-44de-a7d3-452c4ac8fba3", "Aretha", "Franklin", "Oskar-Schindler-Straße",
                23, 86157,
                "Augsburg", true);

        // Shops
        Shop shopA = new Shop("19580231-5901-4a28-b044-6c5c640ff84d", "RoastMarket", false,
                new LinkedList<>(Arrays.asList(giroPayment, paypalPayment)));
        Shop shopB = new Shop("444caf86-eaf2-4952-9360-5b2e1c197b08", "Amazon", false,
                new LinkedList<>(Arrays.asList(giroPayment, paypalPayment)));
        Shop shopX = new Shop("3dcc0461-f11c-451c-8cf3-a728d9f03b34", "Townes", false,
                new LinkedList<>(Arrays.asList(giroPayment, paypalPayment)));
        Shop qualityShop = new Shop("d41bbc01-cdaa-4f98-9346-898658f7d345", "Quality Shop", true,
                new LinkedList<>(Arrays.asList(giroPayment, paypalPayment)));
        Shop dailyShop = new Shop("4263eacf-ec37-42ee-ac02-39c5c245d007", "Daily Shop", true,
                new LinkedList<>(Arrays.asList(giroPayment, paypalPayment)));
        Shop discountShop = new Shop("da0cb604-a95a-4ec1-852b-b91f6db0a1ce", "Discount Shop", true,
                new LinkedList<>(Arrays.asList(giroPayment, paypalPayment)));

        // Smart Orders
        SmartOrder coffeeMild = new SmartOrder("cdde725b-e7a6-4195-a700-43bc7862a76b", "Coffee Mild 500g",
                "Coffee Beans",
                coffeeMachine.getId(), 399, 1399,
                shopA, debitPayment, address1, true, false, false, null);
        SmartOrder waterFilter = new SmartOrder("d5cd0181-259f-4e78-89d9-690550a1b8d9", "Water Filter 10pcs",
                "Water Filters",
                coffeeMachine.getId(), 500, 1500,
                shopB, debitPayment, address1, false, false, false, null);
        SmartOrder blackInk = new SmartOrder("270da9f8-b195-4f45-beae-e308bbdcd635", "Black Ink", "Ink Catridges",
                printer.getId(), 850,
                900,
                shopB, debitPayment, address1, true, true, false, null);
        SmartOrder coloredInk = new SmartOrder("95488674-e579-4b2a-b466-c024a5772bd6", "Colored Ink", "Ink Catridges",
                printer.getId(),
                850, 900,
                shopA, debitPayment, address1, true, true, true, null);

        // Orders
        Order order_001 = new Order(coffeeMild, 399);
        Order order_002 = new Order(waterFilter, 413);
        Order order_003 = new Order(blackInk, 675);
        Order order_004 = new Order(coffeeMild, 675);

        // Link Orders to Smart Orders
        coffeeMild.setOrders(new LinkedList<>(Arrays.asList(order_001.getId(), order_004.getId())));
        waterFilter.setOrders(new LinkedList<>(Arrays.asList(order_002.getId())));
        blackInk.setOrders(new LinkedList<>(Arrays.asList(order_003.getId())));

        // Link Smart Orders to Devices
        coffeeMachine.setSmartOrders(new LinkedList<>(Arrays.asList(coffeeMild, waterFilter)));
        printer.setSmartOrders(new LinkedList<>(Arrays.asList(blackInk, coloredInk)));

        addresses = new LinkedList<>(Arrays.asList(address1, address2));
        paymentMethods = new LinkedList<>(Arrays.asList(debitPayment, giroPayment, paypalPayment));
        orders = new LinkedList<>(Arrays.asList(order_001, order_002, order_003, order_004));
        shops = new LinkedList<>(Arrays.asList(shopA, shopB, shopX, qualityShop, dailyShop, discountShop));
        devices = new LinkedList<>(Arrays.asList(coffeeMachine, printer));
    }

    private int getResourceById(String id, List<?> list) {
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).toString());
            if (list.get(i).toString().equals(id)) {
                return i;
            }
        }
        System.out.println("Resource not found.");
        return -1;
    }

    @GET
    @ResponseStatus(200)
    @Path("/orders")
    public List<Order> getOrders() {
        return orders;
    }

    @POST
    @Path("/orders")
    public void postOrder(Order order) {
        orders.add(order);

        String deviceId = order.getContract().getLinkedTo();
        String smartOrderId = order.getContract().getId();

        int deviceIndex = getResourceById(deviceId, devices);
        if (deviceIndex > -1) {
            Device device = devices.get(deviceIndex);
            int smartOrderIndex = getResourceById(smartOrderId, device.getSmartOrders());
            SmartOrder smartOrder = device.getSmartOrders().get(smartOrderIndex);

            if (smartOrder != null) {
                smartOrder.addOrder(order);
                device.getSmartOrders().set(smartOrderIndex, smartOrder);
                devices.set(deviceIndex, device);
                System.out.println("POST ORDER " + order.getId() + " OF SMART ORDER " + smartOrderId + " SUCCESS.");
                return;
            }

            System.out.println("SMART ORDER " + smartOrderId + " NOT FOUND.");
            return;
        }

        System.out.println("DEVICE " + deviceId + " NOT FOUND.");
        return;
    }

    @POST
    @Path("/devices/{deviceId}/smartorders/")
    public void postSmartOrder(@PathParam("deviceId") String deviceId, SmartOrder smartOrder) {
        int deviceIndex = getResourceById(deviceId, devices);
        Device device = devices.get(deviceIndex);

        if (device != null) {
            device.addSmartOrder(smartOrder);
            devices.set(deviceIndex, device);
            System.out.println("POST SMART ORDER " + smartOrder.getProductName() + " IN DEVICE " + device.getName());
            return;
        }

        System.out.println("DEVICE ID NOT FOUND");
    }

    @PUT
    @Path("/devices/{deviceId}/smartorders/{smartorderId}")
    public void updateSmartOrder(@PathParam("deviceId") String deviceId, @PathParam("smartorderId") String smartOrderId,
            SmartOrder smartOrder) {
        int deviceIndex = getResourceById(deviceId, devices);
        Device device = devices.get(deviceIndex);
        int smartOrderIndex = getResourceById(smartOrderId, device.getSmartOrders());

        device.updateSmartOrder(smartOrderIndex, smartOrder);
        devices.set(deviceIndex, device);

        System.out.println("UPDATE SMART ORDER " + smartOrder.getProductName());
    }

    @DELETE
    @Path("/devices/{deviceId}/smartorders/{smartorderId}")
    public void deleteSmartOrder(@PathParam("deviceId") String deviceId,
            @PathParam("smartorderId") String smartOrderId) {
        int deviceIndex = getResourceById(deviceId, devices);
        Device device = devices.get(deviceIndex);
        int smartOrderIndex = getResourceById(smartOrderId, device.getSmartOrders());

        device.deleteSmartOrder(smartOrderIndex);
        devices.set(deviceIndex, device);

        System.out.println("DELETE SMART ORDER " + device.getSmartOrders().get(smartOrderIndex).getProductName());
    }

    @GET
    @Path("/devices")
    public List<Device> getDevices() {
        return devices;
    }

    @POST
    @Path("/devices")
    public void postDevice(Device device) {
        devices.add(device);

        System.out.println("POST DEVICE " + device.getName());
    }

    @DELETE
    @Path("/devices/{id}")
    public void deleteDevice(@PathParam("id") String id) {
        int deviceIndex = getResourceById(id, devices);
        devices.remove(deviceIndex);

        System.out.println("DELETE DEVICE " + devices.get(deviceIndex).getName());
    }

    @PUT
    @Path("/devices/{id}")
    public void updateDevice(@PathParam("id") String id, Device device) {
        int deviceIndex = getResourceById(id, devices);
        devices.set(deviceIndex, device);

        System.out.println("UPDATE DEVICE " + device.getName());
    }

    @GET
    @Path("/shops")
    public List<Shop> getShops() {
        return shops;
    }

    @PUT
    @Path("/shops/{id}")
    public void updateShop(@PathParam("id") String id, Shop shop) {
        int shopIndex = getResourceById(id, shops);
        shops.set(shopIndex, shop);

        System.out.println("UPDATE SHOP " + shop.getName());
    }

    @GET
    @Path("/addresses")
    public List<Address> getAddresses() {
        return addresses;
    }

    @GET
    @Path("/paymentMethods")
    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

}
