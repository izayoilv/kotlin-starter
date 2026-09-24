package com.example.calc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
          CalculatorScreen()
        }
      }
    }
  }
}

private fun formatResult(value: Double): String =
  if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()

@Composable
fun CalculatorScreen() {
  var display by rememberSaveable { mutableStateOf("0") }
  var firstOperand by rememberSaveable { mutableStateOf<Double?>(null) }
  var pendingOp by rememberSaveable { mutableStateOf<String?>(null) }
  var freshInput by rememberSaveable { mutableStateOf(true) }
  var lastEquation by rememberSaveable { mutableStateOf<String?>(null) }

  fun inputDigit(digit: String) {
    lastEquation = null
    display = if (freshInput || display == "0" || display == "Error") {
      freshInput = false
      digit
    } else {
      display + digit
    }
  }

  fun inputDot() {
    lastEquation = null
    if (freshInput || display == "Error") {
      display = "0."
      freshInput = false
    } else if (!display.contains('.')) {
      display += "."
    }
  }

  fun clear() {
    display = "0"
    firstOperand = null
    pendingOp = null
    freshInput = true
    lastEquation = null
  }

  fun backspace() {
    if (!freshInput && display != "Error") {
      display = if (display.length <= 1) "0" else display.dropLast(1)
    }
  }

  fun negate() {
    if (display != "0" && display != "Error") {
      display = if (display.startsWith("-")) display.drop(1) else "-$display"
    }
  }

  fun calculate() {
    val first = firstOperand ?: return
    val second = display.toDoubleOrNull() ?: return
    val op = pendingOp ?: return
    if (op == "÷" && second == 0.0) {
      display = "Error"
      firstOperand = null
      pendingOp = null
      freshInput = true
      return
    }
    val result = when (op) {
      "+" -> first + second
      "−" -> first - second
      "×" -> first * second
      else -> first / second
    }
    lastEquation = "${formatResult(first)} $op ${formatResult(second)} ="
    display = formatResult(result)
    firstOperand = null
    pendingOp = null
    freshInput = true
  }

  fun setOperator(op: String) {
    if (display == "Error") return
    lastEquation = null
    val current = display.toDoubleOrNull() ?: return
    if (pendingOp != null && !freshInput) {
      calculate()
      firstOperand = display.toDoubleOrNull()
    } else {
      firstOperand = current
    }
    pendingOp = op
    freshInput = true
  }

  Column(modifier = Modifier.fillMaxSize().navigationBarsPadding().padding(16.dp)) {
    val op = pendingOp
    val first = firstOperand
    val expression = if (op != null && first != null) {
      "${formatResult(first)} $op"
    } else {
      lastEquation.orEmpty()
    }
    Box(
      modifier = Modifier.fillMaxWidth().weight(1f),
      contentAlignment = Alignment.BottomEnd,
    ) {
      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = expression,
          style = MaterialTheme.typography.headlineSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
        )
        Text(
          text = display,
          style = MaterialTheme.typography.displayLarge,
          maxLines = 1,
        )
      }
    }
    Spacer(Modifier.height(8.dp))
    CalcRow {
      CalcButton("C", onClick = ::clear)
      CalcButton("⌫", onClick = ::backspace)
      CalcButton("±", onClick = ::negate)
      CalcButton("÷", onClick = { setOperator("÷") }, colors = operatorColors())
    }
    CalcRow {
      CalcButton("7", onClick = { inputDigit("7") })
      CalcButton("8", onClick = { inputDigit("8") })
      CalcButton("9", onClick = { inputDigit("9") })
      CalcButton("×", onClick = { setOperator("×") }, colors = operatorColors())
    }
    CalcRow {
      CalcButton("4", onClick = { inputDigit("4") })
      CalcButton("5", onClick = { inputDigit("5") })
      CalcButton("6", onClick = { inputDigit("6") })
      CalcButton("−", onClick = { setOperator("−") }, colors = operatorColors())
    }
    CalcRow {
      CalcButton("1", onClick = { inputDigit("1") })
      CalcButton("2", onClick = { inputDigit("2") })
      CalcButton("3", onClick = { inputDigit("3") })
      CalcButton("+", onClick = { setOperator("+") }, colors = operatorColors())
    }
    CalcRow {
      CalcButton("0", onClick = { inputDigit("0") }, weight = 2f)
      CalcButton(".", onClick = ::inputDot)
      CalcButton("=", onClick = ::calculate, colors = operatorColors())
    }
  }
}

@Composable
private fun operatorColors(): ButtonColors = ButtonDefaults.buttonColors(
  containerColor = MaterialTheme.colorScheme.primaryContainer,
)

@Composable
fun CalcRow(content: @Composable RowScope.() -> Unit) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    content = content,
  )
  Spacer(Modifier.height(8.dp))
}

@Composable
fun RowScope.CalcButton(
  label: String,
  onClick: () -> Unit,
  weight: Float = 1f,
  colors: ButtonColors = ButtonDefaults.buttonColors(),
) {
  Button(
    onClick = onClick,
    modifier = Modifier.weight(weight).height(72.dp),
    colors = colors,
  ) {
    Text(label, style = MaterialTheme.typography.headlineSmall)
  }
}
