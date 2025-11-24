package com.example.amilimetros.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.amilimetros.ui.viewmodel.AdoptionFormState
import com.example.amilimetros.ui.viewmodel.AdoptionFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptionFormScreen(
    animalId: Long,
    animalName: String,
    viewModel: AdoptionFormViewModel,
    onSubmitSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val formState by viewModel.formState.collectAsState()

    var direccion by remember { mutableStateOf("") }
    var tipoVivienda by remember { mutableStateOf("Casa") }
    var tieneMallasVentanas by remember { mutableStateOf(false) }
    var viveEnDepartamento by remember { mutableStateOf(false) }
    var tieneOtrosAnimales by remember { mutableStateOf(false) }
    var motivoAdopcion by remember { mutableStateOf("") }

    var showSuccessDialog by remember { mutableStateOf(false) }

    val tiposVivienda = listOf("Casa", "Departamento", "Parcela", "Otro")
    var expandedTipoVivienda by remember { mutableStateOf(false) }

    // Manejar éxito
    LaunchedEffect(formState) {
        if (formState is AdoptionFormState.Success) {
            showSuccessDialog = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adoptar a $animalName") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Complete el siguiente formulario para solicitar la adopción",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // DIRECCIÓN
            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección completa") },
                leadingIcon = { Icon(Icons.Default.Home, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // TIPO DE VIVIENDA
            ExposedDropdownMenuBox(
                expanded = expandedTipoVivienda,
                onExpandedChange = { expandedTipoVivienda = !expandedTipoVivienda }
            ) {
                OutlinedTextField(
                    value = tipoVivienda,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de vivienda") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipoVivienda) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedTipoVivienda,
                    onDismissRequest = { expandedTipoVivienda = false }
                ) {
                    tiposVivienda.forEach { tipo ->
                        DropdownMenuItem(
                            text = { Text(tipo) },
                            onClick = {
                                tipoVivienda = tipo
                                expandedTipoVivienda = false
                            }
                        )
                    }
                }
            }

            HorizontalDivider()

            // CHECKBOXES
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = tieneMallasVentanas,
                    onCheckedChange = { tieneMallasVentanas = it }
                )
                Text("¿Tiene mallas en las ventanas?")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = viveEnDepartamento,
                    onCheckedChange = { viveEnDepartamento = it }
                )
                Text("¿Vive en departamento?")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = tieneOtrosAnimales,
                    onCheckedChange = { tieneOtrosAnimales = it }
                )
                Text("¿Tiene otros animales en casa?")
            }

            HorizontalDivider()

            // MOTIVO
            OutlinedTextField(
                value = motivoAdopcion,
                onValueChange = { motivoAdopcion = it },
                label = { Text("¿Por qué deseas adoptarlo?") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 4
            )

            // ERROR
            if (formState is AdoptionFormState.Error) {
                Text(
                    (formState as AdoptionFormState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(8.dp))

            // BOTÓN ENVIAR
            Button(
                onClick = {
                    if (direccion.isBlank()) {
                        // Mostrar error
                        return@Button
                    }
                    viewModel.submitAdoptionForm(
                        animalId = animalId,
                        direccion = direccion,
                        tipoVivienda = tipoVivienda,
                        tieneMallasVentanas = tieneMallasVentanas,
                        viveEnDepartamento = viveEnDepartamento,
                        tieneOtrosAnimales = tieneOtrosAnimales,
                        motivoAdopcion = motivoAdopcion
                    )
                },
                enabled = formState !is AdoptionFormState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (formState is AdoptionFormState.Loading) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Enviando...")
                } else {
                    Icon(Icons.Default.Send, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Enviar Solicitud")
                }
            }
        }
    }

    // DIALOG DE ÉXITO
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("✅ ¡Solicitud Enviada!") },
            text = { Text("Tu solicitud de adopción ha sido enviada exitosamente. Te contactaremos pronto.") },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    viewModel.resetState()
                    onSubmitSuccess()
                }) {
                    Text("Entendido")
                }
            }
        )
    }
}