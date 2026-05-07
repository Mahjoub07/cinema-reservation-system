package com.cinema.pricing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PricingContextTest {

    private PricingContext pricingContext;

    @BeforeEach
    void setUp() {
        List<PricingStrategy> strategies = Arrays.asList(
                new StandardPricingStrategy(),
                new StudentDiscountStrategy(),
                new GroupDiscountStrategy()
        );
        pricingContext = new PricingContext(strategies);
    }

    @Test
    void shouldUseStandardPricingByDefault() {
        double price = pricingContext.calculatePrice(10.0, 2);
        assertEquals(20.0, price, 0.001);
    }

    @Test
    void shouldSwitchToStudentDiscountStrategy() {
        pricingContext.setStrategy("StudentDiscountStrategy");
        double price = pricingContext.calculatePrice(10.0, 2);
        assertEquals(16.0, price, 0.001); // 20% off
    }

    @Test
    void shouldSwitchToGroupDiscountStrategy() {
        pricingContext.setStrategy("GroupDiscountStrategy");
        double price = pricingContext.calculatePrice(10.0, 5);
        assertEquals(42.5, price, 0.001); // 15% off of 50
    }

    @Test
    void shouldNotApplyGroupDiscountForLessThanFiveSeats() {
        pricingContext.setStrategy("GroupDiscountStrategy");
        double price = pricingContext.calculatePrice(10.0, 4);
        assertEquals(40.0, price, 0.001); // no discount
    }

    @Test
    void shouldHandleUnknownStrategyGracefully() {
        pricingContext.setStrategy("NonExistentStrategy");
        double price = pricingContext.calculatePrice(10.0, 2);
        assertEquals(20.0, price, 0.001); // stays on default
    }

    @Test
    void shouldReturnAvailableStrategies() {
        List<String> strategies = pricingContext.getAvailableStrategies();
        assertTrue(strategies.contains("StandardPricingStrategy"));
        assertTrue(strategies.contains("StudentDiscountStrategy"));
        assertTrue(strategies.contains("GroupDiscountStrategy"));
    }

    @Test
    void shouldReturnCurrentStrategyName() {
        assertEquals("StandardPricingStrategy", pricingContext.getCurrentStrategyName());
        pricingContext.setStrategy("StudentDiscountStrategy");
        assertEquals("StudentDiscountStrategy", pricingContext.getCurrentStrategyName());
    }

    @Test
    void shouldHandleEmptyStrategyList() {
        PricingContext emptyContext = new PricingContext(Collections.emptyList());
        assertEquals(20.0, emptyContext.calculatePrice(10.0, 2), 0.001);
        assertEquals("None", emptyContext.getCurrentStrategyName());
    }

    @Test
    void standardPricingShouldReturnZeroForZeroSeats() {
        StandardPricingStrategy strategy = new StandardPricingStrategy();
        assertEquals(0.0, strategy.calculatePrice(10.0, 0), 0.001);
    }

    @Test
    void studentDiscountShouldReturnZeroForZeroSeats() {
        StudentDiscountStrategy strategy = new StudentDiscountStrategy();
        assertEquals(0.0, strategy.calculatePrice(10.0, 0), 0.001);
    }

    @Test
    void groupDiscountShouldReturnZeroForZeroSeats() {
        GroupDiscountStrategy strategy = new GroupDiscountStrategy();
        assertEquals(0.0, strategy.calculatePrice(10.0, 0), 0.001);
    }
}
