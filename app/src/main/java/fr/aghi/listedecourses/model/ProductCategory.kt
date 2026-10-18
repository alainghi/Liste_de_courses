package fr.aghi.listedecourses.model

import androidx.compose.ui.graphics.Color
import com.google.gson.annotations.SerializedName
import kotlin.math.abs

data class ProductCategory(
    @SerializedName("id") val id: String,
    @SerializedName("displayName") val displayName: String
) {
    /**
     * Couleur associée à la catégorie.
     * - Catégories par défaut : couleur fixe, choisie à la main.
     * - Catégories personnalisées : couleur tirée de [CATEGORY_PALETTE],
     *   dérivée de façon stable à partir de l'id (aucun champ à persister).
     */
    val color: Color
        get() = when (id) {
            "FRUITS_LEGUMES" -> Color(0xFF4CAF50)      // vert
            "VIANDES_POISSONS" -> Color(0xFFE53935)    // rouge
            "PRODUITS_LAITIERS" -> Color(0xFFFFC107)   // ambre
            "BOISSONS" -> Color(0xFF1E88E5)            // bleu
            "EPICERIE" -> Color(0xFF795548)            // brun
            "BOULANGERIE" -> Color(0xFFEF6C00)         // orange foncé
            "HYGIENE" -> Color(0xFFAB47BC)             // violet
            "SURGELES" -> Color(0xFF00ACC1)            // cyan
            "PRODUITS_BIO" -> Color(0xFF558B2F)        // olive
            "PETIT_DEJEUNER" -> Color(0xFFFF8F00)      // orange
            "CONSERVES" -> Color(0xFF5D4037)           // brun foncé
            "PLATS_PREPARES" -> Color(0xFFD81B60)      // rose foncé
            "CONDIMENTS_EPICES" -> Color(0xFFBF360C)   // brique
            "PRODUITS_BEBE" -> Color(0xFFF48FB1)       // rose pâle
            "PRODUITS_ENTRETIEN" -> Color(0xFF455A64)  // gris bleuté
            "ANIMALERIE" -> Color(0xFF7CB342)          // vert clair
            "ALCOOLS" -> Color(0xFF6A1B9A)             // pourpre
            "SNACKS" -> Color(0xFF8BC34A)              // lime
            "CONFISERIES" -> Color(0xFFEC407A)         // rose
            "PRODUITS_MONDE" -> Color(0xFF00897B)      // teal
            "CHAUFFAGE" -> Color(0xFFE65100)            // orange braise
            "AUTRES" -> Color(0xFF90A4AE)              // gris
            else -> CATEGORY_PALETTE[abs(id.hashCode()) % CATEGORY_PALETTE.size]
        }

    companion object {
        val FRUITS_LEGUMES = ProductCategory("FRUITS_LEGUMES", "Fruits et Légumes")
        val VIANDES_POISSONS = ProductCategory("VIANDES_POISSONS", "Viandes et Poissons")
        val PRODUITS_LAITIERS = ProductCategory("PRODUITS_LAITIERS", "Produits Laitiers")
        val BOISSONS = ProductCategory("BOISSONS", "Boissons")
        val EPICERIE = ProductCategory("EPICERIE", "Épicerie")
        val BOULANGERIE = ProductCategory("BOULANGERIE", "Boulangerie")
        val HYGIENE = ProductCategory("HYGIENE", "Hygiène")
        val SURGELES = ProductCategory("SURGELES", "Surgelés")
        val PRODUITS_BIO = ProductCategory("PRODUITS_BIO", "Produits Bio")
        val PETIT_DEJEUNER = ProductCategory("PETIT_DEJEUNER", "Petit-déjeuner")
        val CONSERVES = ProductCategory("CONSERVES", "Conserves")
        val PLATS_PREPARES = ProductCategory("PLATS_PREPARES", "Plats préparés")
        val CONDIMENTS_EPICES = ProductCategory("CONDIMENTS_EPICES", "Condiments et Épices")
        val PRODUITS_BEBE = ProductCategory("PRODUITS_BEBE", "Produits pour bébé")
        val PRODUITS_ENTRETIEN = ProductCategory("PRODUITS_ENTRETIEN", "Produits d'entretien")
        val ANIMALERIE = ProductCategory("ANIMALERIE", "Animalerie")
        val ALCOOLS = ProductCategory("ALCOOLS", "Alcools et Spiritueux")
        val SNACKS = ProductCategory("SNACKS", "Snacks et Grignotages")
        val CONFISERIES = ProductCategory("CONFISERIES", "Confiseries et Chocolats")
        val PRODUITS_MONDE = ProductCategory("PRODUITS_MONDE", "Produits du monde")
        val CHAUFFAGE = ProductCategory("CHAUFFAGE", "Chauffage")
        val AUTRES = ProductCategory("AUTRES", "Autres")

        val DEFAULT_CATEGORIES = listOf(
            FRUITS_LEGUMES, VIANDES_POISSONS, PRODUITS_LAITIERS, BOISSONS,
            EPICERIE, BOULANGERIE, HYGIENE, SURGELES, PRODUITS_BIO,
            PETIT_DEJEUNER, CONSERVES, PLATS_PREPARES, CONDIMENTS_EPICES,
            PRODUITS_BEBE, PRODUITS_ENTRETIEN, ANIMALERIE, ALCOOLS,
            SNACKS, CONFISERIES, PRODUITS_MONDE, CHAUFFAGE, AUTRES
        )

        /** Palette de couleurs pour les catégories personnalisées. */
        private val CATEGORY_PALETTE = listOf(
            Color(0xFF4CAF50), Color(0xFFE53935), Color(0xFFFFC107), Color(0xFF1E88E5),
            Color(0xFF795548), Color(0xFFEF6C00), Color(0xFFAB47BC), Color(0xFF00ACC1),
            Color(0xFF558B2F), Color(0xFFFF8F00), Color(0xFFD81B60), Color(0xFFBF360C),
            Color(0xFF9C27B0), Color(0xFF00897B), Color(0xFF3949AB), Color(0xFF7CB342),
            Color(0xFF6A1B9A), Color(0xFF8BC34A), Color(0xFFEC407A), Color(0xFF455A64),
            Color(0xFFFF7043), Color(0xFF5C6BC0), Color(0xFF26A69A), Color(0xFF90A4AE)
        )
    }
}

/**
 * Couleur de texte lisible (noir ou blanc) à utiliser sur [this] comme fond.
 * Basée sur la luminance perçue de la couleur (fonctionne en clair comme en sombre).
 */
val Color.onCategory: Color
    get() = if ((0.299f * red + 0.587f * green + 0.114f * blue) > 0.6f) {
        Color(0xFF212121)
    } else {
        Color.White
    }
