package fr.aghi.listedecourses.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import fr.aghi.listedecourses.model.ShoppingItem
import fr.aghi.listedecourses.model.onCategory

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShoppingList(
    items: List<ShoppingItem>,
    onItemChecked: (ShoppingItem) -> Unit,
    onItemRemoved: (ShoppingItem) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    // Regroupement par catégorie (objet) triée par nom affiché, pour éviter les doublons visuels
    val sortedCategories = items
        .groupBy { it.product.category }
        .toSortedMap(compareBy { it.displayName.lowercase() })
    
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 8.dp,
            top = contentPadding.calculateTopPadding() + 4.dp,
            end = 8.dp,
            bottom = contentPadding.calculateBottomPadding() + 100.dp
        )
    ) {
        sortedCategories.forEach { (category, categoryItems) ->
            // En-tête collant compact teinté de la couleur de la catégorie
            stickyHeader {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = category.color,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        color = category.color.onCategory,
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp)
                    )
                }
            }
            
            items(
                items = categoryItems.sortedBy { it.product.name.lowercase() }, 
                key = { it.id }
            ) { item ->
                ShoppingListItem(
                    item = item,
                    categoryColor = category.color,
                    onCheckedChange = { onItemChecked(item) },
                    onRemove = { onItemRemoved(item) }
                )
                Spacer(modifier = Modifier.height(3.dp))
            }
        }
    }
}

@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    categoryColor: Color,
    onCheckedChange: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isChecked) 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else categoryColor.copy(alpha = 0.22f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (item.isChecked) 0.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = item.isChecked,
                    onCheckedChange = { onCheckedChange() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = categoryColor,
                        checkmarkColor = categoryColor.onCategory
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = item.product.name,
                        style = MaterialTheme.typography.bodyMedium,
                        textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None,
                        color = if (item.isChecked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${item.getDisplayQuantity()} • ${item.product.category.displayName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (item.isChecked) MaterialTheme.colorScheme.onSurfaceVariant else categoryColor
                    )
                }
            }
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
