package com.betting.manager.ui.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.betting.manager.database.MasterHistoryEntity
import com.betting.manager.repository.BettingRepository
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MasterHistoryTab(
    modifier: Modifier = Modifier,
    viewModel: MasterHistoryViewModel = viewModel()
) {
    val history by viewModel.history.collectAsState(initial = emptyList<MasterHistoryEntity>())
    var showReverseDialog by remember { mutableStateOf(false) }
    var selectedHistoryId by remember { mutableStateOf<Long?>(null) }
    var reverseReason by remember { mutableStateOf("") }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Master History",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Icon(
                Icons.Default.History,
                contentDescription = "History",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                title = "Total Forwarded",
                value = history.sumOf { h -> h.forwardedAmount },
                modifier = Modifier.weight(1f)
            )
            
            StatCard(
                title = "Active",
                value = history.filter { h -> !h.isReversed }.sumOf { h -> h.forwardedAmount },
                modifier = Modifier.weight(1f)
            )
            
            StatCard(
                title = "Reversed",
                value = history.filter { h -> h.isReversed }.sumOf { h -> h.forwardedAmount },
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // History list
        if (history.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "No history",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "No master history yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Forward vouchers to see history here",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(history) { historyItem: MasterHistoryEntity ->
                    HistoryCard(
                        history = historyItem,
                        onReverse = {
                            selectedHistoryId = historyItem.id
                            showReverseDialog = true
                        }
                    )
                }
            }
        }
    }
    
    // Reverse dialog
    if (showReverseDialog) {
        AlertDialog(
            onDismissRequest = {
                showReverseDialog = false
                selectedHistoryId = null
                reverseReason = ""
            },
            title = { Text("Reverse History Entry") },
            text = {
                Column {
                    Text("Are you sure you want to reverse this history entry?")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = reverseReason,
                        onValueChange = { reverseReason = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Reason for reversal") },
                        placeholder = { Text("Enter reason...") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (reverseReason.isNotBlank()) {
                            selectedHistoryId?.let { viewModel.reverseHistory(it, reverseReason) }
                            showReverseDialog = false
                            selectedHistoryId = null
                            reverseReason = ""
                        }
                    },
                    enabled = reverseReason.isNotBlank()
                ) {
                    Text("Reverse")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showReverseDialog = false
                        selectedHistoryId = null
                        reverseReason = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: Double,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$${String.format("%.2f", value)}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun HistoryCard(
    history: MasterHistoryEntity,
    onReverse: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (history.isReversed) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Voucher #${history.voucherId}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = formatDate(history.forwardedAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Badge(
                    containerColor = if (history.isReversed) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    },
                    contentColor = if (history.isReversed) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    }
                ) {
                    Text(
                        text = if (history.isReversed) "Reversed" else "Active",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Amount row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Forwarded Amount",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$${String.format("%.2f", history.forwardedAmount)}",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
                
                if (!history.isReversed) {
                    IconButton(
                        onClick = onReverse,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Undo, contentDescription = "Reverse")
                    }
                }
            }
            
            // Reversal info (if reversed)
            if (history.isReversed) {
                Spacer(modifier = Modifier.height(12.dp))
                Column {
                    Text(
                        text = "Reversed on ${formatDate(history.reversedAt ?: 0)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!history.reversalReason.isNullOrBlank()) {
                        Text(
                            text = "Reason: ${history.reversalReason}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return formatter.format(date)
}

class MasterHistoryViewModel(
    private val repository: BettingRepository
) : ViewModel() {
    
    private val _history = MutableStateFlow<List<MasterHistoryEntity>>(emptyList())
    val history: StateFlow<List<MasterHistoryEntity>> = _history
    
    init {
        loadHistory()
    }
    
    private fun loadHistory() {
        viewModelScope.launch {
            repository.getAllMasterHistory().collect { historyList ->
                _history.value = historyList
            }
        }
    }
    
    fun reverseHistory(id: Long, reason: String) {
        viewModelScope.launch {
            repository.reverseMasterHistory(id, reason)
        }
    }
}