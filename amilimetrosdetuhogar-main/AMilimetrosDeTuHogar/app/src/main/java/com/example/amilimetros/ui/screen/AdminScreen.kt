package com.example.amilimetros.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.amilimetros.data.local.product.ProductEntity
import com.example.amilimetros.data.local.animal.AnimalEntity
import com.example.amilimetros.ui.viewmodel.ProductViewModel
import com.example.amilimetros.ui.viewmodel.AnimalViewModel
import com.example.amilimetros.domain.validation.*

@Composable
fun AdminScreen(
    productVm: ProductViewModel,
    animalVm: AnimalViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Productos", "Animales")

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Filled.AdminPanelSettings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "Panel de Administrador",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Gestiona productos y animales",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Tabs
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) },
                    icon = {
                        Icon(
                            if (index == 0) Icons.Filled.Inventory else Icons.Filled.Pets,
                            contentDescription = null
                        )
                    }
                )
            }
        }

        // Content
        when (selectedTab) {
            0 -> AdminProductsTab(productVm)
            1 -> AdminAnimalsTab(animalVm)
        }
    }
}

// ========== TAB DE PRODUCTOS ==========
@Composable
private fun AdminProductsTab(vm: ProductViewModel) {
    val products by vm.products.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Resumen
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Productos",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "${products.size}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                Icon(
                    Icons.Filled.Inventory,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Agregar Producto")
        }

        Spacer(Modifier.height(16.dp))

        if (products.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Filled.Inventory,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "No hay productos registrados",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products) { product ->
                    AdminProductCard(
                        product = product,
                        onEdit = { editingProduct = product },
                        onDelete = { vm.deleteProduct(product) }
                    )
                }
            }
        }
    }

    // Diálogo agregar
    if (showAddDialog) {
        ProductDialog(
            product = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { product ->
                vm.addProduct(product)
                showAddDialog = false
            }
        )
    }

    // Diálogo editar
    editingProduct?.let { product ->
        ProductDialog(
            product = product,
            onDismiss = { editingProduct = null },
            onConfirm = { updated ->
                vm.updateProduct(updated)
                editingProduct = null
            }
        )
    }
}

@Composable
private fun AdminProductCard(
    product: ProductEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "$${"%.2f".format(product.price)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(
                        onClick = { },
                        label = { Text(product.category, style = MaterialTheme.typography.labelSmall) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    )
                    AssistChip(
                        onClick = { },
                        label = { Text("Stock: ${product.stock}", style = MaterialTheme.typography.labelSmall) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (product.stock > 10)
                                MaterialTheme.colorScheme.primaryContainer
                            else if (product.stock > 0)
                                MaterialTheme.colorScheme.secondaryContainer
                            else
                                MaterialTheme.colorScheme.errorContainer
                        ),
                        leadingIcon = {
                            Icon(
                                if (product.stock > 0) Icons.Filled.Inventory else Icons.Filled.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Eliminar Producto") },
            text = { Text("¿Estás seguro de eliminar \"${product.name}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// ========== TAB DE ANIMALES ==========
@Composable
private fun AdminAnimalsTab(vm: AnimalViewModel) {
    val animals by vm.allAnimals.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingAnimal by remember { mutableStateOf<AnimalEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Resumen
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Animales",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "${animals.size}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Disponibles: ${animals.count { !it.isAdopted }}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                Icon(
                    Icons.Filled.Pets,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Agregar Animal")
        }

        Spacer(Modifier.height(16.dp))

        if (animals.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Filled.Pets,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "No hay animales registrados",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(animals) { animal ->
                    AdminAnimalCard(
                        animal = animal,
                        onEdit = { editingAnimal = animal },
                        onDelete = { vm.deleteAnimal(animal) }
                    )
                }
            }
        }
    }

    // Diálogo agregar
    if (showAddDialog) {
        AnimalDialog(
            animal = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { animal ->
                vm.addAnimal(animal)
                showAddDialog = false
            }
        )
    }

    // Diálogo editar
    editingAnimal?.let { animal ->
        AnimalDialog(
            animal = animal,
            onDismiss = { editingAnimal = null },
            onConfirm = { updated ->
                vm.updateAnimal(updated)
                editingAnimal = null
            }
        )
    }
}

@Composable
private fun AdminAnimalCard(
    animal: AnimalEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = animal.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${animal.species} • ${animal.breed} • ${animal.age} año${if (animal.age != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                if (animal.isAdopted) {
                    AssistChip(
                        onClick = { },
                        label = { Text("Adoptado", style = MaterialTheme.typography.labelSmall) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        leadingIcon = {
                            Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                } else {
                    AssistChip(
                        onClick = { },
                        label = { Text("Disponible", style = MaterialTheme.typography.labelSmall) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ),
                        leadingIcon = {
                            Icon(
                                Icons.Filled.FavoriteBorder,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Eliminar Animal") },
            text = {
                Text(
                    if (animal.isAdopted) {
                        "¿Estás seguro de eliminar a \"${animal.name}\"?\n\n⚠️ Este animal ya fue adoptado. Eliminar el registro borrará todo el historial."
                    } else {
                        "¿Estás seguro de eliminar a \"${animal.name}\"?\n\nEsta acción no se puede deshacer."
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// ========== DIÁLOGO DE PRODUCTO CON VALIDACIONES ✅ ==========
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDialog(
    product: ProductEntity?,
    onDismiss: () -> Unit,
    onConfirm: (ProductEntity) -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var description by remember { mutableStateOf(product?.description ?: "") }
    var price by remember { mutableStateOf(product?.price?.toString() ?: "") }
    var stock by remember { mutableStateOf(product?.stock?.toString() ?: "0") }
    var category by remember { mutableStateOf(product?.category ?: "") }

    // ✅ ESTADOS DE ERROR
    var nameError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }
    var stockError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }

    // ✅ VALIDACIÓN EN TIEMPO REAL
    LaunchedEffect(name) { nameError = validateNotEmpty(name, "El nombre") }
    LaunchedEffect(description) { descriptionError = validateDescription(description, 10) }
    LaunchedEffect(price) { priceError = validatePrice(price) }
    LaunchedEffect(stock) { stockError = validateStock(stock) }
    LaunchedEffect(category) { categoryError = validateCategory(category) }

    // ✅ VALIDAR SI PUEDE GUARDAR
    val canSave = nameError == null && descriptionError == null &&
            priceError == null && stockError == null &&
            categoryError == null &&
            name.isNotBlank() && description.isNotBlank() &&
            price.isNotBlank() && stock.isNotBlank() &&
            category.isNotBlank()

    val categories = listOf("Alimento", "Juguetes", "Accesorios", "Higiene", "Salud")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    if (product == null) Icons.Filled.Add else Icons.Filled.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(if (product == null) "Agregar Producto" else "Editar Producto")
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // NOMBRE
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre *") },
                    singleLine = true,
                    isError = nameError != null,
                    supportingText = { if (nameError != null) Text(nameError!!) },
                    modifier = Modifier.fillMaxWidth()
                )

                // DESCRIPCIÓN
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción *") },
                    minLines = 3,
                    maxLines = 5,
                    isError = descriptionError != null,
                    supportingText = {
                        if (descriptionError != null) {
                            Text(descriptionError!!)
                        } else {
                            Text("${description.length} caracteres (mínimo 10)")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // PRECIO
                    OutlinedTextField(
                        value = price,
                        onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) price = it },
                        label = { Text("Precio *") },
                        singleLine = true,
                        isError = priceError != null,
                        supportingText = { if (priceError != null) Text(priceError!!) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        leadingIcon = { Text("$") },
                        modifier = Modifier.weight(1f)
                    )

                    // STOCK
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d+$"))) stock = it },
                        label = { Text("Stock *") },
                        singleLine = true,
                        isError = stockError != null,
                        supportingText = { if (stockError != null) Text(stockError!!) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Icon(Icons.Filled.Inventory, null) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // CATEGORÍA
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Categoría *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        isError = categoryError != null,
                        supportingText = { if (categoryError != null) Text(categoryError!!) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    expanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        when(cat) {
                                            "Alimento" -> Icons.Filled.Restaurant
                                            "Juguetes" -> Icons.Filled.Toys
                                            "Accesorios" -> Icons.Filled.ShoppingBag
                                            "Higiene" -> Icons.Filled.CleanHands
                                            else -> Icons.Filled.HealthAndSafety
                                        },
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }

                // ✅ MENSAJE DE AYUDA
                if (!canSave) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Filled.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                "Completa todos los campos correctamente",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val priceValue = price.toDoubleOrNull() ?: 0.0
                    val stockValue = stock.toIntOrNull() ?: 0

                    onConfirm(
                        ProductEntity(
                            id = product?.id ?: 0L,
                            name = name.trim(),
                            description = description.trim(),
                            price = priceValue,
                            category = category,
                            stock = stockValue
                        )
                    )
                },
                enabled = canSave
            ) {
                Icon(Icons.Filled.Save, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// ========== DIÁLOGO DE ANIMAL CON VALIDACIONES ✅ ==========
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimalDialog(
    animal: AnimalEntity?,
    onDismiss: () -> Unit,
    onConfirm: (AnimalEntity) -> Unit
) {
    var name by remember { mutableStateOf(animal?.name ?: "") }
    var species by remember { mutableStateOf(animal?.species ?: "") }
    var breed by remember { mutableStateOf(animal?.breed ?: "") }
    var age by remember { mutableStateOf(animal?.age?.toString() ?: "") }
    var description by remember { mutableStateOf(animal?.description ?: "") }

    // ✅ ESTADOS DE ERROR
    var nameError by remember { mutableStateOf<String?>(null) }
    var speciesError by remember { mutableStateOf<String?>(null) }
    var breedError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }

    // ✅ VALIDACIÓN EN TIEMPO REAL
    LaunchedEffect(name) { nameError = validateNotEmpty(name, "El nombre") }
    LaunchedEffect(species) { speciesError = validateSpecies(species) }
    LaunchedEffect(breed) { breedError = validateNotEmpty(breed, "La raza") }
    LaunchedEffect(age) { ageError = validateAge(age) }
    LaunchedEffect(description) { descriptionError = validateDescription(description, 10) }

    // ✅ VALIDAR SI PUEDE GUARDAR
    val canSave = nameError == null && speciesError == null &&
            breedError == null && ageError == null &&
            descriptionError == null &&
            name.isNotBlank() && species.isNotBlank() &&
            breed.isNotBlank() && age.isNotBlank() &&
            description.isNotBlank()

    val speciesList = listOf("Perro", "Gato", "Ave", "Conejo", "Otro")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    if (animal == null) Icons.Filled.Add else Icons.Filled.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(if (animal == null) "Agregar Animal" else "Editar Animal")
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // NOMBRE
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre *") },
                    singleLine = true,
                    isError = nameError != null,
                    supportingText = { if (nameError != null) Text(nameError!!) },
                    leadingIcon = { Icon(Icons.Filled.Pets, null) },
                    modifier = Modifier.fillMaxWidth()
                )

                // ESPECIE
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = species,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Especie *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        isError = speciesError != null,
                        supportingText = { if (speciesError != null) Text(speciesError!!) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        speciesList.forEach { sp ->
                            DropdownMenuItem(
                                text = { Text(sp) },
                                onClick = {
                                    species = sp
                                    expanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        when(sp) {
                                            "Perro" -> Icons.Filled.Pets
                                            "Gato" -> Icons.Filled.Pets
                                            "Ave" -> Icons.Filled.Attractions
                                            "Conejo" -> Icons.Filled.Pets
                                            else -> Icons.Filled.Pets
                                        },
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }

                // RAZA
                OutlinedTextField(
                    value = breed,
                    onValueChange = { breed = it },
                    label = { Text("Raza *") },
                    singleLine = true,
                    isError = breedError != null,
                    supportingText = { if (breedError != null) Text(breedError!!) },
                    modifier = Modifier.fillMaxWidth()
                )

                // EDAD
                OutlinedTextField(
                    value = age,
                    onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d+$"))) age = it },
                    label = { Text("Edad (años) *") },
                    singleLine = true,
                    isError = ageError != null,
                    supportingText = { if (ageError != null) Text(ageError!!) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // DESCRIPCIÓN
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción *") },
                    minLines = 3,
                    maxLines = 5,
                    isError = descriptionError != null,
                    supportingText = {
                        if (descriptionError != null) {
                            Text(descriptionError!!)
                        } else {
                            Text("${description.length} caracteres (mínimo 10)")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // ✅ MENSAJE DE AYUDA
                if (!canSave) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Filled.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                "Completa todos los campos correctamente",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val ageValue = age.toIntOrNull() ?: 0
                    onConfirm(
                        AnimalEntity(
                            id = animal?.id ?: 0L,
                            name = name.trim(),
                            species = species,
                            breed = breed.trim(),
                            age = ageValue,
                            description = description.trim(),
                            isAdopted = animal?.isAdopted ?: false
                        )
                    )
                },
                enabled = canSave
            ) {
                Icon(Icons.Filled.Save, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}