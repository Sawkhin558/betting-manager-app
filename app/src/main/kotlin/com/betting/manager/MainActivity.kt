package com.betting.manager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.betting.manager.ui.dashboard.DashboardHeader
import com.betting.manager.ui.tabs.EntryTab
import com.betting.manager.ui.tabs.MasterHistoryTab
import com.betting.manager.ui.tabs.ReportTab
import com.betting.manager.ui.tabs.VouchersTab
import com.betting.manager.repository.BettingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            BettingAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Entry", "Vouchers", "Master", "Report")
    
    val dashboardViewModel: DashboardViewModel = viewModel()
    
    // Collect StateFlows from ViewModel
    val totalSales by dashboardViewModel.totalSales.collectAsState()
    val dailyLimit by dashboardViewModel.dailyLimit.collectAsState()
    val commissionPercentage by dashboardViewModel.commissionPercentage.collectAsState()
    val commissionAmount by dashboardViewModel.commissionAmount.collectAsState()
    val maxPayout by dashboardViewModel.maxPayout.collectAsState()
    val netProfit by dashboardViewModel.netProfit.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Dashboard header
        DashboardHeader(
            totalSales = totalSales,
            dailyLimit = dailyLimit,
            commissionPercentage = commissionPercentage,
            commissionAmount = commissionAmount,
            maxPayout = maxPayout,
            netProfit = netProfit,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Tab content
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> EntryTab()
                1 -> VouchersTab()
                2 -> MasterHistoryTab()
                3 -> ReportTab()
            }
        }
        
        // Bottom navigation
        NavigationBar {
            tabs.forEachIndexed { index, title ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = when (index) {
                                0 -> Icons.Default.Edit
                                1 -> Icons.Default.List
                                2 -> Icons.Default.History
                                3 -> Icons.Default.BarChart
                                else -> Icons.Default.Info
                            },
                            contentDescription = title
                        )
                    },
                    label = { Text(title) },
                    selected = selectedTab == index,
                    onClick = { selectedTab = index }
                )
            }
        }
    }
}

@Composable
fun BettingAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme(),
        typography = MaterialTheme.typography,
        content = content
    )
}

private fun darkColorScheme() = darkColorScheme(
    primary = Color(0xFFBB86FC),
    secondary = Color(0xFF03DAC6),
    tertiary = Color(0xFFCF6679)
)

private fun lightColorScheme() = lightColorScheme(
    primary = Color(0xFF6200EE),
    secondary = Color(0xFF03DAC5),
    tertiary = Color(0xFF018786)
)

// ViewModel for dashboard data
class DashboardViewModel(
    private val repository: BettingRepository
) : ViewModel() {
    
    private val _totalSales = MutableStateFlow(0.0)
    val totalSales: StateFlow<Double> = _totalSales
    
    private val _dailyLimit = MutableStateFlow(10000.0)
    val dailyLimit: StateFlow<Double> = _dailyLimit
    
    private val _commissionPercentage = MutableStateFlow(5.0)
    val commissionPercentage: StateFlow<Double> = _commissionPercentage
    
    private val _commissionAmount = MutableStateFlow(0.0)
    val commissionAmount: StateFlow<Double> = _commissionAmount
    
    private val _maxPayout = MutableStateFlow(0.0)
    val maxPayout: StateFlow<Double> = _maxPayout
    
    private val _netProfit = MutableStateFlow(0.0)
    val netProfit: StateFlow<Double> = _netProfit
    
    init {
        startCollecting()
    }
    
    private fun startCollecting() {
        viewModelScope.launch {
            repository.getSettings().collect { settings ->
                settings?.let {
                    _dailyLimit.value = it.dailyLimit
                    _commissionPercentage.value = it.commissionPercentage
                }
                updateNetProfit()
            }
        }
        viewModelScope.launch {
            repository.getTotalSales().collect { sales ->
                _totalSales.value = sales
                _commissionAmount.value = sales * (_commissionPercentage.value / 100)
                updateNetProfit()
            }
        }
        viewModelScope.launch {
            repository.getTotalSalesAndPayout().collect { salesPayout ->
                _maxPayout.value = salesPayout?.total_potential_payout ?: 0.0
                updateNetProfit()
            }
        }
    }
    
    private fun updateNetProfit() {
        _netProfit.value = (_totalSales.value - _commissionAmount.value) - (_maxPayout.value * 0.01)
    }
    
    fun refresh() {
        // Manual refresh if needed
    }
}
