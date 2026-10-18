package fr.aghi.listedecourses.model

data class ShoppingItem(
    val id: Int,
    val product: Product,
    val quantity: Double,
    val isChecked: Boolean = false
) {
    fun getDisplayQuantity(): String {
        return when (product.unit) {
            "kg", "L" -> {
                if (quantity >= 1.0) "%.1f %s".format(quantity, product.unit)
                else "%.0f g".format(quantity * 1000)
            }
            "g" -> "%.0f %s".format(quantity, product.unit)
            else -> "%.0f %s".format(quantity, product.unit)
        }
    }
} 