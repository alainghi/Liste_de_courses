package fr.aghi.listedecourses.model

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.Normalizer

data class Product(
    val name: String,
    val category: ProductCategory,
    val unit: String = "pièce"
)

object ProductDatabase {
    private val initialProductsList = listOf(
        // Fruits et Légumes
        Product("Abricots", ProductCategory.FRUITS_LEGUMES, "kg"),
        Product("Ail", ProductCategory.FRUITS_LEGUMES),
        Product("Ananas", ProductCategory.FRUITS_LEGUMES),
        Product("Asperges", ProductCategory.FRUITS_LEGUMES, "botte"),
        Product("Aubergine", ProductCategory.FRUITS_LEGUMES, "kg"),
        Product("Avocats", ProductCategory.FRUITS_LEGUMES),
        Product("Bananes", ProductCategory.FRUITS_LEGUMES, "kg"),
        Product("Betteraves", ProductCategory.FRUITS_LEGUMES, "kg"),
        Product("Brocoli", ProductCategory.FRUITS_LEGUMES, "kg"),
        Product("Carottes", ProductCategory.FRUITS_LEGUMES, "kg"),
        Product("Citrons", ProductCategory.FRUITS_LEGUMES, "kg"),
        Product("Concombre", ProductCategory.FRUITS_LEGUMES),
        Product("Courgettes", ProductCategory.FRUITS_LEGUMES, "kg"),
        Product("Épinards", ProductCategory.FRUITS_LEGUMES, "kg"),
        Product("Fraises", ProductCategory.FRUITS_LEGUMES, "barquette"),
        Product("Framboises", ProductCategory.FRUITS_LEGUMES, "barquette"),
        Product("Haricots verts", ProductCategory.FRUITS_LEGUMES, "kg"),
        Product("Kiwi", ProductCategory.FRUITS_LEGUMES, "kg"),
        Product("Mangue", ProductCategory.FRUITS_LEGUMES),
        Product("Melon", ProductCategory.FRUITS_LEGUMES),
        Product("Myrtilles", ProductCategory.FRUITS_LEGUMES, "barquette"),
        Product("Navets", ProductCategory.FRUITS_LEGUMES, "kg"),
        Product("Oignons", ProductCategory.FRUITS_LEGUMES, "kg"),
        Product("Oranges", ProductCategory.FRUITS_LEGUMES, "kg"),
        Product("Pastèque", ProductCategory.FRUITS_LEGUMES, "kg"),
        Product("Patate douce", ProductCategory.FRUITS_LEGUMES, "kg"),
        Product("Pêches", ProductCategory.FRUITS_LEGUMES, "kg"),
        Product("Poireaux", ProductCategory.FRUITS_LEGUMES, "kg"),
        Product("Poires", ProductCategory.FRUITS_LEGUMES, "kg"),
        Product("Poivrons", ProductCategory.FRUITS_LEGUMES, "kg"),
        Product("Pommes", ProductCategory.FRUITS_LEGUMES, "kg"),
        Product("Pommes de terre", ProductCategory.FRUITS_LEGUMES, "kg"),
        Product("Radis", ProductCategory.FRUITS_LEGUMES, "botte"),
        Product("Raisins", ProductCategory.FRUITS_LEGUMES, "kg"),
        Product("Salade", ProductCategory.FRUITS_LEGUMES),
        Product("Tomates", ProductCategory.FRUITS_LEGUMES, "kg"),

        // Viandes et Poissons
        Product("Bacon", ProductCategory.VIANDES_POISSONS, "tranches"),
        Product("Chipolatas", ProductCategory.VIANDES_POISSONS, "kg"),
        Product("Chorizo", ProductCategory.VIANDES_POISSONS),
        Product("Crevettes", ProductCategory.VIANDES_POISSONS, "kg"),
        Product("Jambon", ProductCategory.VIANDES_POISSONS, "tranches"),
        Product("Lardons", ProductCategory.VIANDES_POISSONS, "g"),
        Product("Merguez", ProductCategory.VIANDES_POISSONS, "kg"),
        Product("Œufs", ProductCategory.VIANDES_POISSONS, "boîte"),
        Product("Poisson blanc", ProductCategory.VIANDES_POISSONS, "kg"),
        Product("Poulet", ProductCategory.VIANDES_POISSONS, "kg"),
        Product("Saumon", ProductCategory.VIANDES_POISSONS, "kg"),
        Product("Saucisses", ProductCategory.VIANDES_POISSONS, "kg"),
        Product("Steak haché", ProductCategory.VIANDES_POISSONS, "kg"),
        Product("Thon en conserve", ProductCategory.VIANDES_POISSONS),

        // Produits Laitiers
        Product("Beurre", ProductCategory.PRODUITS_LAITIERS, "g"),
        Product("Camembert", ProductCategory.PRODUITS_LAITIERS),
        Product("Comté", ProductCategory.PRODUITS_LAITIERS, "g"),
        Product("Crème fraîche", ProductCategory.PRODUITS_LAITIERS, "mL"),
        Product("Emmental", ProductCategory.PRODUITS_LAITIERS, "g"),
        Product("Fromage râpé", ProductCategory.PRODUITS_LAITIERS, "g"),
        Product("Lait", ProductCategory.PRODUITS_LAITIERS, "L"),
        Product("Mozzarella", ProductCategory.PRODUITS_LAITIERS, "g"),
        Product("Yaourt nature", ProductCategory.PRODUITS_LAITIERS, "pack"),

        // Boissons
        Product("Café", ProductCategory.BOISSONS, "g"),
        Product("Coca-Cola", ProductCategory.BOISSONS, "L"),
        Product("Eau gazeuse", ProductCategory.BOISSONS, "L"),
        Product("Eau plate", ProductCategory.BOISSONS, "L"),
        Product("Jus d'orange", ProductCategory.BOISSONS, "L"),
        Product("Thé", ProductCategory.BOISSONS, "boîte"),

        // Boulangerie
        Product("Baguette", ProductCategory.BOULANGERIE),
        Product("Brioche", ProductCategory.BOULANGERIE),
        Product("Croissants", ProductCategory.BOULANGERIE),
        Product("Pain au chocolat", ProductCategory.BOULANGERIE),
        Product("Pain complet", ProductCategory.BOULANGERIE),
        Product("Pain de mie", ProductCategory.BOULANGERIE, "paquet"),

        // Épicerie
        Product("Chocolat", ProductCategory.EPICERIE, "tablette"),
        Product("Farine", ProductCategory.EPICERIE, "kg"),
        Product("Huile d'olive", ProductCategory.EPICERIE, "L"),
        Product("Miel", ProductCategory.EPICERIE, "pot"),
        Product("Pâtes", ProductCategory.EPICERIE, "kg"),
        Product("Riz", ProductCategory.EPICERIE, "kg"),
        Product("Sel", ProductCategory.EPICERIE, "kg"),
        Product("Sucre", ProductCategory.EPICERIE, "kg"),

        // Hygiène et Entretien
        Product("Dentifrice", ProductCategory.HYGIENE),
        Product("Gel douche", ProductCategory.HYGIENE),
        Product("Lessive", ProductCategory.HYGIENE, "L"),
        Product("Papier toilette", ProductCategory.HYGIENE, "pack"),
        Product("Savon", ProductCategory.HYGIENE),
        Product("Shampoing", ProductCategory.HYGIENE),
        Product("Sopalin", ProductCategory.PRODUITS_ENTRETIEN, "rouleau")
    )
    private val initialProductNames = initialProductsList.map { it.name.lowercase() }.toSet()

    private val _categories = mutableListOf<ProductCategory>().apply {
        addAll(ProductCategory.DEFAULT_CATEGORIES)
    }
    val categories: List<ProductCategory> 
        get() = _categories.toList().sortedBy { it.displayName.lowercase() }

    private val _products = mutableListOf<Product>().apply {
        addAll(initialProductsList)
    }
    val products: List<Product> 
        get() = _products.toList().sortedBy { it.name.lowercase() }

    fun addCustomCategory(name: String): ProductCategory {
        val trimmed = name.trim()
        val existing = _categories.find { it.displayName.equals(trimmed, ignoreCase = true) }
        if (existing != null) return existing
        val newCategory = ProductCategory("CUSTOM_${System.currentTimeMillis()}", trimmed)
        _categories.add(newCategory)
        return newCategory
    }

    fun canRemoveCategory(category: ProductCategory): Boolean {
        return !ProductCategory.DEFAULT_CATEGORIES.any { it.id == category.id }
    }

    fun removeCategory(category: ProductCategory) {
        if (!canRemoveCategory(category)) return
        _categories.removeAll { it.id == category.id }
        _products.removeAll { it.category.id == category.id }
    }

    fun addCustomProduct(name: String, category: ProductCategory, unit: String): Product {
        _products.removeAll { it.name.equals(name, ignoreCase = true) }
        val newProduct = Product(name, category, unit)
        _products.add(newProduct)
        return newProduct
    }

    fun canRemoveProduct(product: Product): Boolean {
        return !(initialProductNames.contains(product.name.lowercase()) && !product.category.id.startsWith("CUSTOM_"))
    }

    fun removeProduct(product: Product) {
        if (!canRemoveProduct(product)) return
        _products.removeAll { it.name.equals(product.name, ignoreCase = true) }
    }

    fun updateProductCategory(product: Product, newCategory: ProductCategory) {
        val index = _products.indexOfFirst { it.name.equals(product.name, ignoreCase = true) }
        if (index != -1) {
            val oldProduct = _products[index]
            _products[index] = oldProduct.copy(category = newCategory)
        }
    }

    fun searchProducts(query: String): List<Product> {
        if (query.isBlank()) return emptyList()
        val normalizedQuery = normalizeAccents(query.trim().lowercase())
        return _products
            .map { product -> Pair(product, searchScore(product.name, normalizedQuery)) }
            .filter { entry -> entry.second >= 0 }
            .sortedWith(compareBy({ it.second }, { it.first.name.lowercase() }))
            .map { entry -> entry.first }
    }

    /**
     * Score de pertinence pour la recherche floue : plus petit = meilleur match.
     * -0 si aucune correspondance.
     * 0 = le nom commence par la requête
     * 1 = le nom contient la requête
     * 2 = un mot du nom est proche (distance de Levenshtein, tolère les fautes de frappe)
     * 3 = la requête est une sous-séquence du nom
     */
    private fun searchScore(name: String, query: String): Int {
        val normalized = normalizeAccents(name.lowercase())
        if (normalized.startsWith(query)) return 0
        if (normalized.contains(query)) return 1
        if (query.length >= 3) {
            val threshold = if (query.length <= 4) 1 else 2
            val words = normalized.split(Regex("\\W+")).filter { it.isNotEmpty() }
            if (words.any { levenshtein(it, query) <= threshold }) return 2
        }
        if (isSubsequence(query, normalized)) return 3
        return -1
    }

    private fun isSubsequence(query: String, target: String): Boolean {
        if (query.length < 3) return false
        var index = 0
        for (ch in target) {
            if (ch == query[index]) index++
            if (index == query.length) return true
        }
        return false
    }

    private fun levenshtein(a: String, b: String): Int {
        if (a.isEmpty()) return b.length
        if (b.isEmpty()) return a.length
        var previous = IntArray(b.length + 1) { it }
        for (i in a.indices) {
            val current = IntArray(b.length + 1) { it + 1 }
            for (j in b.indices) {
                current[j + 1] = minOf(
                    previous[j + 1] + 1,
                    current[j] + 1,
                    previous[j] + if (a[i] == b[j]) 0 else 1
                )
            }
            previous = current
        }
        return previous[b.length]
    }

    /** Retire les accents pour que "eclair" retrouve "Éclair". */
    private fun normalizeAccents(s: String): String {
        return Normalizer.normalize(s, Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "")
    }

    fun save(context: Context) {
        val sharedPrefs = context.getSharedPreferences("product_db_prefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val customProducts = _products.filter { !initialProductNames.contains(it.name.lowercase()) || it.category.id.startsWith("CUSTOM_") }
        val customCategories = _categories.filter { it.id.startsWith("CUSTOM_") }
        sharedPrefs.edit().apply {
            putString("all_products", gson.toJson(customProducts))
            putString("all_categories", gson.toJson(customCategories))
            apply()
        }
    }

    fun load(context: Context) {
        val sharedPrefs = context.getSharedPreferences("product_db_prefs", Context.MODE_PRIVATE)
        val gson = Gson()
        sharedPrefs.getString("all_categories", null)?.let { json ->
            val type = object : TypeToken<List<ProductCategory>>() {}.type
            val loaded: List<ProductCategory> = gson.fromJson(json, type) ?: emptyList()
            loaded.forEach { cat -> if (_categories.none { c -> c.id == cat.id }) _categories.add(cat) }
        }
        sharedPrefs.getString("all_products", null)?.let { json ->
            val type = object : TypeToken<List<Product>>() {}.type
            val loaded: List<Product> = gson.fromJson(json, type) ?: emptyList()
            loaded.forEach { prod ->
                _products.removeAll { p -> p.name.equals(prod.name, ignoreCase = true) }
                _products.add(prod)
            }
        }
    }
}
