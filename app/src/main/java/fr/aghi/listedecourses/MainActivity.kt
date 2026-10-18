package fr.aghi.listedecourses

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.aghi.listedecourses.data.ShoppingListStorage
import fr.aghi.listedecourses.model.Product
import fr.aghi.listedecourses.model.ProductCategory
import fr.aghi.listedecourses.model.ProductDatabase
import fr.aghi.listedecourses.model.ShoppingItem
import fr.aghi.listedecourses.ui.components.CategoryDot
import fr.aghi.listedecourses.ui.components.CategoryManagementScreen
import fr.aghi.listedecourses.ui.components.ManagementScreen
import fr.aghi.listedecourses.ui.components.ProductManagementScreen
import fr.aghi.listedecourses.ui.components.ShoppingList
import fr.aghi.listedecourses.ui.theme.ListeDeCoursesTheme

class MainActivity : ComponentActivity() {
    private lateinit var storage: ShoppingListStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storage = ShoppingListStorage(this)
        ProductDatabase.load(this)
        enableEdgeToEdge()
        setContent {
            ListeDeCoursesTheme {
                ShoppingApp(storage)
            }
        }
    }
}

@Composable
fun ShoppingApp(storage: ShoppingListStorage? = null) {
    val context = LocalContext.current
    val localStorage = storage ?: remember { ShoppingListStorage(context) }
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "shopping_list") {
        composable("shopping_list") {
            ShoppingListScreen(navController = navController, storage = localStorage)
        }
        composable("management") {
            ManagementScreen(navController)
        }
        composable("category_management") {
            CategoryManagementScreen(navController)
        }
        composable("product_management") {
            ProductManagementScreen(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(navController: NavController, storage: ShoppingListStorage) {
    val context = LocalContext.current
    var items by remember { mutableStateOf(storage.loadShoppingList()) }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(items) {
        storage.saveShoppingList(items)
    }

    Scaffold(
        topBar = {
                TopAppBar(
                    title = { Text("Liste de Courses") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                    ),
                    actions = {
                        if (items.isNotEmpty()) {
                            IconButton(onClick = { shareShoppingList(context, items) }) {
                                Icon(Icons.Default.Share, contentDescription = "Partager la liste")
                            }
                        }
                        if (items.any { it.isChecked }) {
                            IconButton(onClick = { items = items.filter { !it.isChecked } }) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = "Vider les cochés")
                            }
                        }
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Ajouter")
                        }
                        IconButton(onClick = { navController.navigate("management") }) {
                            Icon(Icons.Default.Settings, contentDescription = "Gestion")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                if (items.isEmpty()) {
                    Text(
                        text = "Votre liste est vide\nAppuyez sur + pour ajouter un article",
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                } else {
                    ShoppingList(
                        items = items,
                        onItemChecked = { item ->
                            items = items.map { if (it.id == item.id) it.copy(isChecked = !it.isChecked) else it }
                        },
                        onItemRemoved = { item ->
                            items = items.filter { it.id != item.id }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            if (showAddDialog) {
                AddItemDialog(
                    onDismiss = { showAddDialog = false },
                    onConfirm = { product, quantity ->
                        val existing = items.find {
                            it.product.name.equals(product.name, ignoreCase = true)
                        }
                        if (existing != null) {
                            // Fusion : on augmente la quantité de l'entrée existante
                            items = items.map {
                                if (it.id == existing.id) it.copy(quantity = it.quantity + quantity) else it
                            }
                        } else {
                            val newItem = ShoppingItem(
                                id = (items.maxOfOrNull { it.id } ?: 0) + 1,
                                product = product,
                                quantity = quantity
                            )
                            items = items + newItem
                        }
                        showAddDialog = false
                    }
                )
            }
        }
}

/**
 * Formate la liste de courses en texte partageable et ouvre le menu de partage
 * (SMS, WhatsApp, e-mail...), groupé par catégorie :
 *   - [ ] Lait x2 (L)
 */
private fun shareShoppingList(context: Context, items: List<ShoppingItem>) {
    val grouped = items
        .groupBy { it.product.category.displayName }
        .toSortedMap(compareBy { it.lowercase() })

    val sb = StringBuilder("🛒 Liste de courses\n")
    grouped.forEach { (categoryName, categoryItems) ->
        sb.append("\n").append(categoryName).append(":\n")
        categoryItems
            .sortedBy { it.product.name.lowercase() }
            .forEach { item ->
                val check = if (item.isChecked) "x" else " "
                val quantity = item.quantity
                val quantityText = if ((quantity % 1.0) == 0.0) {
                    quantity.toInt().toString()
                } else {
                    quantity.toString().replace('.', ',')
                }
                sb.append("- [").append(check).append("] ")
                    .append(item.product.name)
                    .append(" x").append(quantityText)
                    .append(" (").append(item.product.unit).append(")\n")
            }
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Liste de courses")
        putExtra(Intent.EXTRA_TEXT, sb.toString())
    }
    context.startActivity(Intent.createChooser(intent, "Partager la liste"))
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (Product, Double) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var quantityStr by remember { mutableStateOf("1") }
    var showSuggestions by remember { mutableStateOf(false) }
    var customUnit by remember { mutableStateOf("pièce") }
    var selectedCategory by remember { mutableStateOf<ProductCategory?>(null) }
    var showNewCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    val suggestions = remember(searchQuery) {
        ProductDatabase.searchProducts(searchQuery)
    }
    
    val focusRequester = remember { FocusRequester() }
    
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter un article") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = selectedProduct?.name ?: searchQuery,
                    onValueChange = { 
                        searchQuery = it
                        selectedProduct = null
                        showSuggestions = true
                    },
                    label = { Text("Nom de l'article") },
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    singleLine = true
                )
                
                if (showSuggestions && suggestions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Suggestions :", style = MaterialTheme.typography.labelMedium)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        suggestions.take(8).forEach { product ->
                            SuggestionChip(
                                onClick = {
                                    selectedProduct = product
                                    searchQuery = product.name
                                    customUnit = product.unit
                                    showSuggestions = false
                                },
                                label = { Text(product.name) }
                            )
                        }
                    }
                }
                
                if (selectedProduct == null && searchQuery.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Unités rapides :", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("kg", "L", "pack", "pièce").forEach { unit ->
                            FilterChip(
                                selected = customUnit == unit,
                                onClick = { customUnit = unit },
                                label = { Text(unit) }
                            )
                        }
                    }
                    
                    OutlinedTextField(
                        value = customUnit,
                        onValueChange = { customUnit = it },
                        label = { Text("Autre unité") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Catégorie :", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    OutlinedButton(
                        onClick = { showNewCategoryDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Nouvelle catégorie")
                    }
                    FlowRow(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ProductDatabase.categories.forEach { category ->
                            val isSelected = selectedCategory?.id == category.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = category },
                                label = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CategoryDot(
                                            color = category.color,
                                            size = 8.dp,
                                            modifier = Modifier.padding(end = 4.dp)
                                        )
                                        Text(category.displayName)
                                    }
                                },
                                colors = if (isSelected)
                                    FilterChipDefaults.filterChipColors(
                                        containerColor = category.color.copy(alpha = 0.25f),
                                        labelColor = category.color,
                                        iconColor = category.color
                                    )
                                else
                                    FilterChipDefaults.filterChipColors(
                                        iconColor = category.color
                                    )
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("Quantité :", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FilledTonalIconButton(onClick = {
                        val current = quantityStr.toDoubleOrNull() ?: 1.0
                        if (current > 0.5) {
                            val next = if (current % 1.0 == 0.0) current - 1.0 else current - 0.5
                            quantityStr = if (next % 1.0 == 0.0) next.toInt().toString() else next.toString()
                        }
                    }) {
                        Icon(Icons.Default.Remove, contentDescription = "Moins")
                    }
                    
                    OutlinedTextField(
                        value = quantityStr,
                        onValueChange = { newValue -> 
                            if (newValue.isEmpty() || newValue.matches(Regex("^\\d*[,.]?\\d*$"))) {
                                quantityStr = newValue.replace(",", ".")
                            }
                        },
                        modifier = Modifier.width(100.dp),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center, fontWeight = FontWeight.Bold),
                        singleLine = true
                    )
                    
                    FilledTonalIconButton(onClick = {
                        val current = quantityStr.toDoubleOrNull() ?: 0.0
                        val next = if (current % 1.0 == 0.0) current + 1.0 else current + 0.5
                        quantityStr = if (next % 1.0 == 0.0) next.toInt().toString() else next.toString()
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Plus")
                    }
                }
                
                if (selectedProduct != null) {
                    Text(
                        text = "Unité : ${selectedProduct!!.unit}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp).align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            val isNewProduct = selectedProduct == null && searchQuery.isNotBlank()
            val isQuantityValid = quantityStr.isNotBlank() && quantityStr.toDoubleOrNull() != null
            
            Button(
                onClick = {
                    val parsedQuantity = quantityStr.toDoubleOrNull()
                    if (parsedQuantity != null) {
                        val productToUse = if (selectedProduct != null) {
                            selectedProduct!!
                        } else {
                            val category = selectedCategory
                                ?: return@Button
                            val newProd = ProductDatabase.addCustomProduct(searchQuery, category, customUnit)
                            ProductDatabase.save(context)
                            newProd
                        }
                        onConfirm(productToUse, parsedQuantity)
                    }
                },
                enabled = isQuantityValid && (selectedProduct != null || (isNewProduct && selectedCategory != null))
            ) {
                Text("Ajouter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )

    // Sous-dialog pour saisir le nom de la nouvelle catégorie
    if (showNewCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showNewCategoryDialog = false },
            title = { Text("Nouvelle catégorie") },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("Nom de la catégorie") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = newCategoryName.trim()
                        if (name.isNotBlank()) {
                            val category = ProductDatabase.categories.find { it.displayName.equals(name, ignoreCase = true) }
                                ?: ProductDatabase.addCustomCategory(name)
                            ProductDatabase.save(context)
                            selectedCategory = category
                        }
                        showNewCategoryDialog = false
                    },
                    enabled = newCategoryName.isNotBlank()
                ) {
                    Text("Créer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewCategoryDialog = false }) { Text("Annuler") }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingAppPreview() {
    ListeDeCoursesTheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "shopping_list") {
            composable("shopping_list") {
                ShoppingListScreen(navController = navController, storage = ShoppingListStorage(LocalContext.current))
            }
            composable("management") { ManagementScreen(navController) }
            composable("category_management") { CategoryManagementScreen(navController) }
            composable("product_management") { ProductManagementScreen(navController) }
        }
    }
}
