package no.ikov.orderservice.infrastructure.init;

import net.datafaker.Faker;
import no.ikov.orderservice.domain.model.DeliveryAddress;
import no.ikov.orderservice.domain.model.Order;
import no.ikov.orderservice.domain.model.OrderItem;
import no.ikov.orderservice.domain.model.OrderStatus;
import no.ikov.orderservice.domain.model.Price;
import no.ikov.orderservice.domain.repository.OrderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Random;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private static final Currency EUR = Currency.getInstance("EUR");

    private static final OrderStatus[] WEIGHTED_STATUSES = {
            OrderStatus.CREATED, OrderStatus.CREATED, OrderStatus.CREATED,
            OrderStatus.CONFIRMED, OrderStatus.CONFIRMED, OrderStatus.CONFIRMED,
            OrderStatus.IN_DELIVERY, OrderStatus.IN_DELIVERY,
            OrderStatus.DELIVERED, OrderStatus.DELIVERED, OrderStatus.DELIVERED,
            OrderStatus.CANCELLED
    };

    private record Product(Long id, String name, BigDecimal price) {}

    private static final List<Product> CATALOG = List.of(
            new Product(1L,  "AMD Ryzen 9 7950X",                     new BigDecimal("599.00")),
            new Product(2L,  "Intel Core i9-13900K",                  new BigDecimal("549.00")),
            new Product(3L,  "AMD Ryzen 5 7600X",                     new BigDecimal("249.00")),
            new Product(4L,  "Intel Core i5-13600K",                  new BigDecimal("299.00")),
            new Product(5L,  "NVIDIA GeForce RTX 4090 24GB",          new BigDecimal("1799.00")),
            new Product(6L,  "AMD Radeon RX 7900 XTX 24GB",           new BigDecimal("999.00")),
            new Product(7L,  "NVIDIA GeForce RTX 4070 Ti 12GB",       new BigDecimal("799.00")),
            new Product(8L,  "AMD Radeon RX 7800 XT 16GB",            new BigDecimal("499.00")),
            new Product(9L,  "Corsair Vengeance 32GB DDR5-6000",      new BigDecimal("149.00")),
            new Product(10L, "Kingston Fury Beast 16GB DDR5-5200",    new BigDecimal("79.00")),
            new Product(11L, "G.Skill Trident Z5 64GB DDR5-6400",     new BigDecimal("289.00")),
            new Product(12L, "Samsung 990 Pro 2TB NVMe SSD",          new BigDecimal("179.00")),
            new Product(13L, "WD Black SN850X 1TB NVMe SSD",          new BigDecimal("119.00")),
            new Product(14L, "Crucial P5 Plus 500GB NVMe SSD",        new BigDecimal("69.00")),
            new Product(15L, "ASUS ROG Strix X670E-E Gaming WiFi",    new BigDecimal("499.00")),
            new Product(16L, "MSI MEG Z790 ACE",                      new BigDecimal("449.00")),
            new Product(17L, "Gigabyte B650 AORUS Elite AX",          new BigDecimal("249.00")),
            new Product(18L, "Corsair RM1000x 1000W 80+ Gold",        new BigDecimal("179.00")),
            new Product(19L, "be quiet! Straight Power 12 850W",      new BigDecimal("149.00")),
            new Product(20L, "EVGA SuperNOVA 750 G6 80+ Gold",        new BigDecimal("129.00")),
            new Product(21L, "Fractal Design Define 7 ATX Tower",     new BigDecimal("169.00")),
            new Product(22L, "Lian Li PC-O11 Dynamic EVO",            new BigDecimal("139.00")),
            new Product(23L, "NZXT H510 Flow Mid Tower",              new BigDecimal("89.00")),
            new Product(24L, "Noctua NH-D15 CPU Cooler",              new BigDecimal("99.00")),
            new Product(25L, "be quiet! Dark Rock Pro 4",             new BigDecimal("79.00")),
            new Product(26L, "Corsair iCUE H150i Elite LCD 360mm",    new BigDecimal("219.00")),
            new Product(27L, "ARCTIC Freezer 34 eSports DUO",         new BigDecimal("39.00")),
            new Product(28L, "Logitech MX Keys Advanced Keyboard",    new BigDecimal("109.00")),
            new Product(29L, "Razer DeathAdder V3 Gaming Mouse",      new BigDecimal("89.00")),
            new Product(30L, "Samsung 27\" Odyssey G7 QHD 240Hz",     new BigDecimal("499.00"))
    );

    private final OrderRepository orderRepository;

    public DataInitializer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(String... args) {
        Faker faker = new Faker();
        Random random = new Random(42);

        for (int i = 0; i < 20; i++) {
            long customerId = random.nextLong(1, 101);

            DeliveryAddress address = new DeliveryAddress(
                    faker.address().streetAddress(),
                    faker.address().city(),
                    faker.address().zipCode(),
                    faker.address().country()
            );

            int itemCount = random.nextInt(1, 5);
            List<Product> shuffled = new ArrayList<>(CATALOG);
            Collections.shuffle(shuffled, random);

            List<OrderItem> items = new ArrayList<>();
            for (int j = 0; j < itemCount; j++) {
                Product product = shuffled.get(j);
                int quantity = random.nextInt(1, 4);
                items.add(new OrderItem(product.id(), product.name(), quantity, new Price(product.price(), EUR)));
            }

            Order order = new Order(customerId, address, items);

            OrderStatus status = WEIGHTED_STATUSES[random.nextInt(WEIGHTED_STATUSES.length)];
            if (status != OrderStatus.CREATED) {
                order.transitionTo(status);
            }

            orderRepository.save(order);
        }
    }
}
