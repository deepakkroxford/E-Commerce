package com.example.eCommerce.repositories;

import com.example.eCommerce.model.Cart;
import com.example.eCommerce.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface  CartItemRepository extends JpaRepository<CartItem,Long> {
    @Query("SELECT ci FROM CartItem  ci WHERE ci.cart.cartId= ?1 AND ci.product.productId = ?2")
  CartItem findCartItemByProductIdAndCartId(Long cardId, Long productId);

    @Modifying
    @Query("DELETE FROM CartItem ci where  ci.cart.cartId = ?1 AND ci.product.productId =?2")
    void deleteCartItemByProductIdAndCartId(Long cartId, Long productId);


    @Query("DELETE FROM CartItem ci WHERE ci.cart.cartId=?1")
    List<Cart> deleteAllByCartId(Long cartId);
}
