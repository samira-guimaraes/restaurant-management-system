/**
 * The OrderStatus enum represents the various statuses that an order can have
 * throughout its lifecycle in the system. These statuses enable tracking and
 * managing the progress of an order from initiation to completion or cancellation.
 *
 * Each constant within the enum corresponds to a specific stage in the order lifecycle:
 *
 * - PENDING: The order has been created but no further actions have been taken.
 * - PREPARING: The order is actively being prepared.
 * - READY: The order has been prepared and is ready for delivery or pickup.
 * - DELIVERED: The order has been successfully delivered to the customer.
 * - CANCELED: The order has been canceled before completion.
 * - COMPLETED: The order process has been finalized, typically marked after delivery or fulfillment.
 *
 */
public enum OrderStatus {
    PENDING,
    PREPARING,
    READY,
    DELIVERED,
    CANCELED,
    COMPLETED;

    private OrderStatus() {
    }
}
