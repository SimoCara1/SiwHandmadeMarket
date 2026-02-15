package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.*;
import it.uniroma3.siw.repository.*;
import it.uniroma3.siw.service.CredentialsService;
import jakarta.transaction.Transactional;

@Controller
public class ProductController {

    @Autowired private ProductRepository productRepository;
    @Autowired private CredentialsService credentialsService;
    @Autowired private CartRepository cartRepository;

    // --- LOGICA PRODOTTI ---

    @GetMapping("/products")
    public String showAllProducts(Model model) {
        model.addAttribute("products", this.productRepository.findAll());
        return "products";
    }

    @GetMapping("/products/{id}")
    public String showProduct(@PathVariable("id") Long id, Model model) {
        Product product = this.productRepository.findById(id).orElse(null);
        model.addAttribute("product", product);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            User user = credentialsService.getCredentials(userDetails.getUsername()).getUser();
            model.addAttribute("loggedInUser", user);

            // CONTROLLO: Il prodotto è già nel carrello dell'utente?
            Cart cart = cartRepository.findByUser(user).orElse(null);
            boolean alreadyInCart = false;
            if (cart != null && product != null) {
                alreadyInCart = cart.getProducts().contains(product);
            }
            model.addAttribute("alreadyInCart", alreadyInCart);
        }
        return "productDetail";
    }

    @GetMapping("/admin/productForm")
    public String showProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "productForm";
    }

    @PostMapping("/admin/products")
    public String saveProduct(@ModelAttribute("product") Product product, 
                              @RequestParam("file") MultipartFile[] files) throws IOException {
        List<Image> images = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                Image image = new Image();
                image.setData(file.getBytes());
                image.setName(file.getOriginalFilename());
                images.add(image);
            }
        }
        product.setImages(images);
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
        product.setAuthor(credentials.getUser());
        this.productRepository.save(product);
        return "redirect:/products";
    }

    @GetMapping("/admin/products/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        model.addAttribute("product", product);
        return "productEditForm";
    }

    @PostMapping("/admin/products/update/{id}")
    public String updateProduct(@PathVariable("id") Long id, @ModelAttribute("product") Product product) {
        Product existing = productRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setName(product.getName());
            existing.setDescription(product.getDescription());
            existing.setPrice(product.getPrice());
            existing.setCategory(product.getCategory());
            productRepository.save(existing);
        }
        return "redirect:/products/" + id;
    }
    
    @PostMapping("/admin/products/delete/{id}")
    @Transactional // Fondamentale per gestire più operazioni sul DB in sicurezza
    public String deleteProduct(@PathVariable("id") Long id) {
        Product product = this.productRepository.findById(id).orElse(null);
        
        if (product != null) {
            // 1. Sicurezza: recuperiamo l'utente loggato
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserDetails ud = (UserDetails) auth.getPrincipal();
            User currentUser = credentialsService.getCredentials(ud.getUsername()).getUser();

            // 2. Controllo Autore: solo chi ha creato il prodotto può eliminarlo
            if (product.getAuthor().getId().equals(currentUser.getId())) {
                
                // 3. RECUPERIAMO TUTTI I CARRELLI che contengono questo prodotto
                List<Cart> carts = (List<Cart>) this.cartRepository.findAll();
                for (Cart cart : carts) {
                    if (cart.getProducts().contains(product)) {
                        cart.getProducts().remove(product);
                        this.cartRepository.save(cart); // Sganciamo il prodotto dal carrello
                    }
                }

                // 4. ELIMINAZIONE FISICA del prodotto e delle sue immagini
                this.productRepository.delete(product);
            }
        }
        return "redirect:/products";
    }

    @GetMapping("/cart")
    public String showCart(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            UserDetails ud = (UserDetails) auth.getPrincipal();
            Credentials creds = credentialsService.getCredentials(ud.getUsername());
            User user = creds.getUser();
            
            Cart cart = cartRepository.findByUser(user).orElse(new Cart(user));
            
            Double total = cart.getProducts().stream()
                               .mapToDouble(Product::getPrice)
                               .sum();
            
            model.addAttribute("cart", cart);
            model.addAttribute("totalPrice", String.format("%.2f", total));
            return "cart";
        }
        return "redirect:/login";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("productId") Long productId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = credentialsService.getCredentials(userDetails.getUsername()).getUser();

        Cart cart = cartRepository.findByUser(user).orElse(new Cart(user));
        if (cart.getUser() == null) cart.setUser(user);

        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
        		if (!cart.getProducts().contains(product)) {
            cart.getProducts().add(product);
            cartRepository.save(cart);
        		}
        }
        return "redirect:/cart";
    }
    
    @GetMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable("id") Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal(); //
        User user = credentialsService.getCredentials(userDetails.getUsername()).getUser(); //

        Cart cart = cartRepository.findByUser(user).orElse(null);
        if (cart != null) {
            // Cerchiamo il prodotto nella lista del carrello
            Product product = productRepository.findById(id).orElse(null);
            if (product != null) {
                cart.getProducts().remove(product); // Rimuove l'associazione Many-to-Many
                cartRepository.save(cart); // Aggiorna il DB
            }
        }
        return "redirect:/cart";
    }
    
    @GetMapping("/my-products")
    public String showMyProducts(Model model) {
        // 1. Recupero l'utente loggato
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User currentUser = credentialsService.getCredentials(userDetails.getUsername()).getUser();

        List<Product> myProducts = this.productRepository.findByAuthor(currentUser);
        
        model.addAttribute("products", myProducts);
        return "myProducts";
    }
}