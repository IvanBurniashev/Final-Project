package com.example.finalproject.service;

import com.example.finalproject.dto.requestdto.CartItemRequestDto;

import com.example.finalproject.dto.responsedto.*;
import com.example.finalproject.entity.*;
import com.example.finalproject.entity.enums.Role;
import com.example.finalproject.exception.DataAlreadyExistsException;
import com.example.finalproject.exception.DataNotFoundInDataBaseException;
import com.example.finalproject.mapper.Mappers;
import com.example.finalproject.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private ProductRepository productRepositoryMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private CartRepository cartRepositoryMock;

    @Mock
    public CartItemRepository cartItemRepositoryMock;

    @Mock
    private Mappers mappersMock;

    @InjectMocks
    private CartService cartServiceMock;

    DataNotFoundInDataBaseException dataNotFoundInDataBaseException;
    DataAlreadyExistsException dataAlreadyExistsException;

    private User user;
    private Cart cart;
    private CartItem cartItem;
    private Product product;

    private ProductResponseDto productResponseDto;
    private UserResponseDto userResponseDto;
    private CartResponseDto cartResponseDto;
    private CartItemResponseDto cartItemResponseDto;

    private CartItemRequestDto cartItemRequestDto, wrongCartItemRequestDto, existingCartItemRequestDto;

    @BeforeEach
    void setUp() {

//Entity
        user = new User(1L,
                "Arne Oswald",
                "arneoswald@example.com",
                "+496151226",
                "Pass1$trong",
                Role.CLIENT,
                null,
                null,
                null);

        cart = new Cart(1L, null, user);


        product = new Product(1L,
                "Name",
                "Description",
                new BigDecimal("100.00"),
                new BigDecimal("0.00"),
                "http://localhost/img/1.jpg",
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now()),
                new Category(1L, "Category", null),
                null,
                null,
                null);

        cartItem = new CartItem(1L, product, 2, cart);
        Set<CartItem> cartItemSet = new HashSet<>();
        cartItemSet.add(cartItem);
        cart.setCartItems(cartItemSet);
        user.setCart(cart);

//ResponseDto
        userResponseDto = UserResponseDto.builder()
                .userId(1L)
                .name("Arne Oswald")
                .email("arneoswald@example.com")
                .phone("+496151226")
                .password("Pass1$trong")
                .role(Role.CLIENT)
                .build();

        cartResponseDto = CartResponseDto.builder()
                .cartId(1L)
                .userResponseDto(userResponseDto)
                .build();

        productResponseDto = ProductResponseDto.builder()
                .productId(1L)
                .name("Name")
                .description("Description")
                .price(new BigDecimal("100.00"))
                .imageUrl("http://localhost/img/1.jpg")
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .categoryResponseDto(new CategoryResponseDto(1L, "Category"))
                .build();

        cartItemResponseDto = CartItemResponseDto.builder()
                .cartItemId(1L)
                .cartResponseDto(cartResponseDto)
                .productResponseDto(productResponseDto)
                .quantity(5)
                .build();

//RequestDto
        cartItemRequestDto = CartItemRequestDto.builder()
                .productId(2L)
                .quantity(5)
                .build();

        wrongCartItemRequestDto = CartItemRequestDto.builder()
                .productId(66L)
                .quantity(5)
                .build();

        existingCartItemRequestDto = CartItemRequestDto.builder()
                .productId(1L)
                .quantity(5)
                .build();
    }

    @Test
    void getCartItemsByUserId() {
        Long id = 1L;
        Long wrongId = 58L;

        when(userRepositoryMock.findById(id)).thenReturn(Optional.of(user));
        when(mappersMock.convertToCartItemResponseDto(any(CartItem.class))).thenReturn(cartItemResponseDto);
        Set<CartItemResponseDto> cartItemResponseDtoSet = new HashSet<>();
        cartItemResponseDtoSet.add(cartItemResponseDto);
        Set<CartItemResponseDto> actualCartItemSet = cartServiceMock.getCartItemsByUserId(id);

        verify(userRepositoryMock, times(1)).findById(id);
        verify(mappersMock, times(1)).convertToCartItemResponseDto(any(CartItem.class));

        assertFalse(actualCartItemSet.isEmpty());
        assertEquals(cartItemResponseDtoSet.size(), actualCartItemSet.size());
        assertEquals(cartItemResponseDtoSet.hashCode(), actualCartItemSet.hashCode());

        when(userRepositoryMock.findById(wrongId)).thenReturn(Optional.empty());
        dataNotFoundInDataBaseException = assertThrows(DataNotFoundInDataBaseException.class,
                () -> cartServiceMock.getCartItemsByUserId(wrongId));
        assertEquals("User not found in database.", dataNotFoundInDataBaseException.getMessage());
    }

    @Test
    void insertCartItem() {
        Long id = 1L;
        Long wrongId = 58L;

        when(userRepositoryMock.findById(id)).thenReturn(Optional.of(user));
        when(productRepositoryMock.findById(cartItemRequestDto.getProductId())).thenReturn(Optional.of(product));
        when(cartRepositoryMock.findById(user.getCart().getCartId())).thenReturn(Optional.of(cart));

        cartServiceMock.insertCartItem(cartItemRequestDto, id);

        verify(cartItemRepositoryMock, times(1)).save(any(CartItem.class));


        when(userRepositoryMock.findById(wrongId)).thenReturn(Optional.empty());
        dataNotFoundInDataBaseException = assertThrows(DataNotFoundInDataBaseException.class,
                () -> cartServiceMock.insertCartItem(cartItemRequestDto, wrongId));
        assertEquals("User not found in database.", dataNotFoundInDataBaseException.getMessage());

        when(productRepositoryMock.findById(wrongCartItemRequestDto.getProductId())).thenReturn(Optional.empty());
        dataNotFoundInDataBaseException = assertThrows(DataNotFoundInDataBaseException.class,
                () -> cartServiceMock.insertCartItem(wrongCartItemRequestDto, id));
        assertEquals("Product not found in database.", dataNotFoundInDataBaseException.getMessage());


        when(productRepositoryMock.findById(existingCartItemRequestDto.getProductId())).thenReturn(Optional.of(product));
        dataAlreadyExistsException = assertThrows(DataAlreadyExistsException.class,
                () -> cartServiceMock.insertCartItem(existingCartItemRequestDto, id));
        assertEquals("This product is already in Cart.", dataAlreadyExistsException.getMessage());
    }

    @Test
    void deleteCarItemByProductId() {

        Long userId = 1L;
        Long wrongUserId = 75L;

        Long productId = 1L;
        Long wrongProductId = 75L;

        when(userRepositoryMock.findById(userId)).thenReturn(Optional.of(user));
        when(productRepositoryMock.findById(productId)).thenReturn(Optional.of(product));

        cartServiceMock.deleteCarItemByProductId(userId, productId);

        verify(userRepositoryMock, times(1)).findById(userId);
        verify(productRepositoryMock, times(1)).findById(productId);
        verify(cartItemRepositoryMock, times(1)).deleteById(user.getCart().getCartItems().iterator().next().getCartItemId());


        when(userRepositoryMock.findById(wrongUserId)).thenReturn(Optional.empty());
        dataNotFoundInDataBaseException = assertThrows(DataNotFoundInDataBaseException.class,
                () -> cartServiceMock.deleteCarItemByProductId(wrongUserId, productId));
        assertEquals("User not found in database.", dataNotFoundInDataBaseException.getMessage());

        when(productRepositoryMock.findById(wrongProductId)).thenReturn(Optional.empty());
        dataNotFoundInDataBaseException = assertThrows(DataNotFoundInDataBaseException.class,
                () -> cartServiceMock.deleteCarItemByProductId(userId, wrongProductId));
        assertEquals("Product not found in database.", dataNotFoundInDataBaseException.getMessage());
    }
}