package com.cinema.decorator;

import org.springframework.stereotype.Component;

/**
 * Factory for assembling Decorator chains.
 * Provides pre-configured movie descriptor stacks without
 * modifying existing service classes.
 */
@Component
public class MovieDescriptorFactory {

    private final BaseMovieDescriptor baseDescriptor;

    public MovieDescriptorFactory(BaseMovieDescriptor baseDescriptor) {
        this.baseDescriptor = baseDescriptor;
    }

    /**
     * Creates a simple descriptor with no enhancements.
     */
    public MovieDescriptor createSimpleDescriptor() {
        return baseDescriptor;
    }

    /**
     * Creates a descriptor with badges and ratings.
     * Demonstrates the decorator chain: Base → Badge → Rating.
     */
    public MovieDescriptor createEnhancedDescriptor() {
        MovieDescriptor descriptor = baseDescriptor;
        descriptor = new BadgeDecorator(descriptor);
        descriptor = new RatingDecorator(descriptor);
        return descriptor;
    }

    /**
     * Creates a descriptor with only badge decoration.
     */
    public MovieDescriptor createBadgeDescriptor() {
        return new BadgeDecorator(baseDescriptor);
    }
}
