package com.example.tipingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tipingapp.components.InputField
import com.example.tipingapp.util.calculateTotalPerPerson
import com.example.tipingapp.util.calculateTotalTip
import com.example.tipingapp.widgets.RoundIconButton
import kotlin.math.absoluteValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TipingApp{
                MainContent()
            }
        }
    }
}

@Composable
fun TipingApp(content: @Composable () -> Unit){

    Surface(color = MaterialTheme.colorScheme.background) {
        content()
    }
}
@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 134.0){
    Surface(modifier = Modifier.fillMaxWidth()
        .height(150.dp)
        .padding(14.dp)
        .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)
    ) {
        Column(modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            val total = "%.2f".format(totalPerPerson)
            Text(text = "Total Per Person",
                style = MaterialTheme.typography.titleMedium)
            Text(text = "$total $",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold)
        }
    }
}
@Preview
@Composable
fun MainContent(){

    Column(modifier = Modifier.padding(all = 12.dp)){
        BillForm()
    }
}

@Preview
@Composable
fun BillForm(modifier: Modifier = Modifier,
             onValChange: (String) -> Unit = {}){
    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember{
        mutableFloatStateOf(0f)
    }

    val splitByState = remember{
        mutableIntStateOf(1)
    }

    val tipPercentage = (sliderPositionState.floatValue * 100).toInt()

    val tipAmountState = remember {
        mutableDoubleStateOf(0.0)
    }

    val totalPerPersonState = remember {
        mutableDoubleStateOf(0.0)
    }

    Surface(modifier = Modifier.padding(2.dp).fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp , color = Color.LightGray)
    ) {
        Column(modifier = Modifier.padding(6.dp),
                verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start) {
            TopHeader(totalPerPerson = totalPerPersonState.doubleValue)
            InputField(valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions{
                    if(!validState) return@KeyboardActions
                    onValChange(totalBillState.value.trim())
                    keyboardController?.hide()
                }
            )
            if(validState){
                Row(modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start) {
                    Text("Split",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically))
                    Spacer(modifier = Modifier.width(140.dp))
                    Row(modifier = Modifier.padding(horizontal = 3.dp),
                    horizontalArrangement = Arrangement.End){
                        RoundIconButton(imageVector = Icons.Default.Remove,
                            onClick = {
                                if (splitByState.intValue > 1) {
                                    splitByState.intValue -= 1
                                }
                                val currentTipPercentage = (sliderPositionState.floatValue * 100).toInt()
                                totalPerPersonState.doubleValue = calculateTotalPerPerson(
                                    totalBillState.value.toDoubleOrNull() ?: 0.0,
                                    splitBy = splitByState.intValue,
                                    tipPercentage = currentTipPercentage
                                )
                            })

                        Text(text = "${splitByState.intValue}",
                            modifier = Modifier.align(Alignment.CenterVertically).padding(start = 9.dp, end = 9.dp),
                            style = MaterialTheme.typography.titleLarge)

                        RoundIconButton(imageVector = Icons.Default.Add,
                            onClick = {
                                if (splitByState.intValue < 10) {
                                    splitByState.intValue += 1
                                }

                                val currentTipPercentage = (sliderPositionState.floatValue * 100).toInt()
                                totalPerPersonState.doubleValue = calculateTotalPerPerson(
                                    totalBillState.value.toDoubleOrNull() ?: 0.0,
                                    splitBy = splitByState.intValue,
                                    tipPercentage = currentTipPercentage
                                )
                            })
                    }
                }
                Row(modifier = Modifier.padding(6.dp),
                    horizontalArrangement = Arrangement.Start) {
                    Text(text = "Tip",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically))
                    Spacer(modifier = Modifier.width(200.dp))
                    Text(text = "${tipAmountState.doubleValue} $",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically),
                        style = MaterialTheme.typography.titleMedium)
                }
                Column(verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(text = "${tipPercentage.absoluteValue} %",
                        modifier = Modifier.padding(top = 14.dp))

                    Spacer(modifier = Modifier.height(14.dp))

                    Slider(value = sliderPositionState.floatValue,
                        onValueChange = { newVal ->
                            sliderPositionState.floatValue = newVal
                                        tipAmountState.doubleValue = calculateTotalTip(totalBillState.value.toDouble(), tipPercentage)
                                        totalPerPersonState.doubleValue =
                                        calculateTotalPerPerson(totalBillState.value.toDouble(),
                                                                splitBy = splitByState.intValue,
                                                                tipPercentage = tipPercentage)
                        },
                        modifier = Modifier.padding(start = 12.dp, end = 12.dp)

                        )

                }
            } else {
                Box() {}
            }
        }
    }
}

