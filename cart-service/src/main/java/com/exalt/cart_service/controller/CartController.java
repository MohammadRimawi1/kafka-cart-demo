package com.exalt.cart_service.controller;

import com.exalt.cart_service.model.Cart;
import com.exalt.cart_service.model.CartItem;
import com.exalt.cart_service.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Exposes the two REST endpoints for cart-service:
 *   - adding an item to a cart (creating one if needed)
 *   - checking a cart out
 *
 * This class contains no business logic of its own. It only
 * translates HTTP requests into calls on CartService and wraps
 * the result in a ResponseEntity.
 *
 * @author Mohammad Rimawi
 */
@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Adds an item to a cart.
     *
     * POST /api/carts/items?cartId=<uuid>   (cartId optional)
     * Body: { "productId": 101, "quantity": 2 }
     *
     * If cartId is omitted, a new cart is created and its id
     * is returned in the response body.
     *
     * @param cartId optional existing cart id
     * @param item   the item to add, from the request body
     * @return the updated cart, including its cartId
     */
    @PostMapping("/items")
    public ResponseEntity<Cart> addItem(
            @RequestParam(required = false) UUID cartId,
            @RequestBody CartItem item) {

        Cart cart = cartService.addItemToCart(cartId, item);
        return ResponseEntity.ok(cart);
    }

    /**
     * Checks out a cart.
     *
     * POST /api/carts/{cartId}/checkout
     *
     * @param cartId the cart to check out
     * @return the checked-out cart
     */
    @PostMapping("/{cartId}/checkout")
    public ResponseEntity<Cart> checkout(@PathVariable UUID cartId) {
        Cart cart = cartService.checkoutCart(cartId);
        return ResponseEntity.ok(cart);
    }
}