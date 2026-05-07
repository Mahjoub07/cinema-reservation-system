package com.cinema.decorator;

import com.cinema.dto.MovieDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MovieDescriptorTest {

    private MovieDTO movie;
    private BaseMovieDescriptor baseDescriptor;

    @BeforeEach
    void setUp() {
        movie = new MovieDTO(
                1L, "Inception", "Mind-bending thriller", "Sci-Fi",
                148, LocalDateTime.now(), 100, "poster.jpg", 12.50
        );
        baseDescriptor = new BaseMovieDescriptor();
    }

    @Test
    void baseDescriptorShouldReturnDescription() {
        String desc = baseDescriptor.describe(movie);
        assertTrue(desc.contains("Inception"));
        assertTrue(desc.contains("Sci-Fi"));
        assertTrue(desc.contains("Mind-bending thriller"));
    }

    @Test
    void baseDescriptorShouldHandleNullDescription() {
        MovieDTO noDescMovie = new MovieDTO(
                2L, "Avatar", null, "Sci-Fi",
                162, LocalDateTime.now(), 50, null, 10.0
        );
        String desc = baseDescriptor.describe(noDescMovie);
        assertTrue(desc.contains("No description"));
    }

    @Test
    void badgeDecoratorShouldAddBargainBadge() {
        movie.setPrice(3.0); // below 5.0 threshold
        BadgeDecorator decorator = new BadgeDecorator(baseDescriptor);
        String desc = decorator.describe(movie);
        assertTrue(desc.contains("[BARGAIN]"));
    }

    @Test
    void badgeDecoratorShouldAddSellingFastBadge() {
        movie.setAvailableSeats(3); // 5 or less
        BadgeDecorator decorator = new BadgeDecorator(baseDescriptor);
        String desc = decorator.describe(movie);
        assertTrue(desc.contains("[SELLING FAST]"));
    }

    @Test
    void badgeDecoratorShouldAddPlentyOfSeatsBadge() {
        movie.setAvailableSeats(60); // above 50
        BadgeDecorator decorator = new BadgeDecorator(baseDescriptor);
        String desc = decorator.describe(movie);
        assertTrue(desc.contains("[PLENTY OF SEATS]"));
    }

    @Test
    void badgeDecoratorShouldAddNowShowingBadgeByDefault() {
        movie.setPrice(8.0);
        movie.setAvailableSeats(10);
        BadgeDecorator decorator = new BadgeDecorator(baseDescriptor);
        String desc = decorator.describe(movie);
        assertTrue(desc.contains("[NOW SHOWING]"));
    }

    @Test
    void ratingDecoratorShouldAddRating() {
        RatingDecorator decorator = new RatingDecorator(baseDescriptor);
        String desc = decorator.describe(movie);
        assertTrue(desc.contains("Rating:"));
    }

    @Test
    void ratingDecoratorShouldBeDeterministic() {
        RatingDecorator decorator = new RatingDecorator(baseDescriptor);
        String desc1 = decorator.describe(movie);
        String desc2 = decorator.describe(movie);
        assertEquals(desc1, desc2);
    }

    @Test
    void chainedDecoratorShouldHaveBadgeAndRating() {
        MovieDescriptor descriptor = new RatingDecorator(new BadgeDecorator(baseDescriptor));
        String desc = descriptor.describe(movie);
        assertTrue(desc.contains("Rating:"));
        assertTrue(desc.contains("[PLENTY OF SEATS]"));
    }

    @Test
    void movieDescriptorFactoryShouldCreateSimpleDescriptor() {
        MovieDescriptorFactory factory = new MovieDescriptorFactory(baseDescriptor);
        MovieDescriptor descriptor = factory.createSimpleDescriptor();
        assertNotNull(descriptor);
        String desc = descriptor.describe(movie);
        assertTrue(desc.contains("Inception"));
    }

    @Test
    void movieDescriptorFactoryShouldCreateEnhancedDescriptor() {
        MovieDescriptorFactory factory = new MovieDescriptorFactory(baseDescriptor);
        MovieDescriptor descriptor = factory.createEnhancedDescriptor();
        String desc = descriptor.describe(movie);
        assertTrue(desc.contains("Rating:"));
        assertTrue(desc.contains("[PLENTY OF SEATS]"));
    }

    @Test
    void movieDescriptorFactoryShouldCreateBadgeDescriptor() {
        MovieDescriptorFactory factory = new MovieDescriptorFactory(baseDescriptor);
        MovieDescriptor descriptor = factory.createBadgeDescriptor();
        String desc = descriptor.describe(movie);
        assertTrue(desc.contains("[PLENTY OF SEATS]"));
        assertFalse(desc.contains("Rating:"));
    }
}
