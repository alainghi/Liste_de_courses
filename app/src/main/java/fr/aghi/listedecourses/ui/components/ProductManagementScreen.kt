package fr.aghi.listedecourses.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.aghi.listedecourses.model.Product
import fr.aghi.listedecourses.model.ProductDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductManagementScreen(navController: NavController) {
    val context = LocalContext.current
    var products by remember { mutableStateOf(ProductDatabase.products) }
    var productToEditCategory by remember { mutableStateOf<Product?>(null) }

    // Rafraîchir la liste à l'ouverture
    LaunchedEffect(Unit) {
        products = ProductDatabase.products
    }

    var showAddProductDialog by remember { mutableStateOf(false) }
    var newProductName by remember { mutableStateOf("") }
    var newProductUnit by remember { mutableStateOf("pièce") }
    var newProductCategoryName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gérer les articles") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddProductDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Ajouter un produit")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding()
            )
        ) {
            items(products, key = { "${it.name}-${it.category.id}" }) { product ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(product.name, style = MaterialTheme.typography.bodyLarge)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CategoryDot(
                                color = product.category.color,
                                size = 8.dp,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text(
                                text = "${product.category.displayName} • ${product.unit}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = { productToEditCategory = product }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Modifier la catégorie",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (ProductDatabase.canRemoveProduct(product)) {
                        IconButton(onClick = { 
                            ProductDatabase.removeProduct(product)
                            ProductDatabase.save(context)
                            products = ProductDatabase.products
                         }) {
                            Icon(
                                imageVector = Icons.Default.Delete, 
                                contentDescription = "Supprimer",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                HorizontalDivider()
            }
            // Espace de sécurité final pour forcer le scroll
            item {
                Spacer(modifier = Modifier.height(150.dp))
            }
        }
    }

    if (showAddProductDialog) {
        AlertDialog(
            onDismissRequest = { showAddProductDialog = false },
            title = { Text("Nouveau produit") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(
                        value = newProductName,
                        onValueChange = { newProductName = it },
                        label = { Text("Nom du produit") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newProductUnit,
                        onValueChange = { newProductUnit = it },
                        label = { Text("Unité (kg, pièce...)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newProductCategoryName,
                        onValueChange = { newProductCategoryName = it },
                        label = { Text("Catégorie") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newProductName.isNotBlank() && newProductCategoryName.isNotBlank()) {
                            val category = ProductDatabase.categories.find { 
                                it.displayName.equals(newProductCategoryName, ignoreCase = true) 
                            } ?: ProductDatabase.addCustomCategory(newProductCategoryName)
                            
                            ProductDatabase.addCustomProduct(newProductName, category, newProductUnit)
                            ProductDatabase.save(context)
                            products = ProductDatabase.products
                            
                            newProductName = ""
                            newProductUnit = "pièce"
                            newProductCategoryName = ""
                            showAddProductDialog = false
                        }
                    },
                    enabled = newProductName.isNotBlank() && newProductCategoryName.isNotBlank()
                ) {
                    Text("Ajouter")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddProductDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    if (productToEditCategory != null) {
        AlertDialog(
            onDismissRequest = { productToEditCategory = null },
            title = { Text("Modifier la catégorie de ${productToEditCategory!!.name}") },
            text = {
                // Utilisation d'un conteneur Box avec heightIn pour s'assurer que le contenu défile correctement
                Box(modifier = Modifier.fillMaxWidth().heightIn(max = 280.dp)) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(ProductDatabase.categories, key = { it.id }) { category ->
                            val isSelected = category.id == productToEditCategory!!.category.id
                            TextButton(
                                onClick = {
                                    ProductDatabase.updateProductCategory(productToEditCategory!!, category)
                                    ProductDatabase.save(context)
                                    products = ProductDatabase.products
                                    productToEditCategory = null
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = if (isSelected) ButtonDefaults.textButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else ButtonDefaults.textButtonColors()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    CategoryDot(
                                        color = category.color,
                                        size = 10.dp,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(
                                        text = category.displayName,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { productToEditCategory = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}
