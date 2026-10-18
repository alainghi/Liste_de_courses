package fr.aghi.listedecourses.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.aghi.listedecourses.model.ProductDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(navController: NavController) {
    val context = LocalContext.current
    var categories by remember { mutableStateOf(ProductDatabase.categories) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    // Rafraîchissement garanti à l'affichage
    LaunchedEffect(Unit) {
        categories = ProductDatabase.categories
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gérer les catégories") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddCategoryDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Ajouter")
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
            // Liste triée via le getter ProductDatabase.categories
            items(categories, key = { it.id }) { category ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        CategoryDot(
                            color = category.color,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(category.displayName, style = MaterialTheme.typography.bodyLarge)
                    }
                    if (ProductDatabase.canRemoveCategory(category)) {
                        IconButton(onClick = { 
                            ProductDatabase.removeCategory(category)
                            ProductDatabase.save(context)
                            categories = ProductDatabase.categories // Mise à jour de l'état
                         }) {
                            Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
                HorizontalDivider()
            }
            // Marge de sécurité finale pour forcer le scroll au maximum
            item {
                Spacer(modifier = Modifier.height(150.dp))
            }
        }
    }

    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Nouvelle catégorie") },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("Nom de la catégorie") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoryName.isNotBlank()) {
                            ProductDatabase.addCustomCategory(newCategoryName)
                            ProductDatabase.save(context)
                            categories = ProductDatabase.categories // Mise à jour de l'état
                            newCategoryName = ""
                            showAddCategoryDialog = false
                        }
                    },
                    enabled = newCategoryName.isNotBlank()
                ) {
                    Text("Ajouter")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}
