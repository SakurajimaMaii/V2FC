/*
 * Copyright 2023 VastGui guihy2019@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.v2fc.vastgui.app.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.v2fc.vastgui.app.R

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2023/11/4

data class Bank(
    val organization: String,
    val number: String,
    val expire: String,
    val signature: String,
    @DrawableRes val brand: Int
)

class CreditCardColors internal constructor(
    internal val containerColor: Color,
    internal val contentColor: Color,
    internal val magneticStripeColor: Color,
    internal val signatureAreaColor: Color,
    internal val onSignatureAreaColor: Color
)

object CreditCardDefaults {

    @Composable
    internal fun creditCardColors(
        containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
        contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
        magneticStripeColor: Color = Color.Black,
        signatureAreaColor: Color = Color.White,
        onSignatureAreaColor: Color = Color.Black
    ): CreditCardColors = CreditCardColors(
        containerColor,
        contentColor,
        magneticStripeColor,
        signatureAreaColor,
        onSignatureAreaColor
    )

}

@Preview(showBackground = true)
@Composable
fun CreditCard(
    bank: Bank = Bank(
        "MasterCard",
        "1234 5678 9101 1121",
        "2023-11",
        "123",
        R.drawable.ic_ms_symbol
    ),
    colors: CreditCardColors = CreditCardDefaults.creditCardColors(contentColor = Color.White),
    @DrawableRes background: Int? = R.drawable.img_bridge,
    animationSpec: AnimationSpec<Float> = tween(500)
) {
    var rotated by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (rotated) 180f else 0f,
        animationSpec = animationSpec,
        label = "CreditCard Rotation"
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (!rotated) 1f else 0f,
        animationSpec = animationSpec,
        label = "CreditCard Content Alpha Animation"
    )
    val cardHolder = buildAnnotatedString {
        withStyle(SpanStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold)) {
            append("Card Holder\n")
        }
        withStyle(SpanStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold)) {
            append("Card Holder")
        }
    }
    val cardDate = buildAnnotatedString {
        withStyle(SpanStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold)) {
            append("VALID THRU\n")
        }
        withStyle(SpanStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold)) {
            append(bank.expire)
        }
    }
    Card(
        modifier = Modifier
            .wrapContentSize()
            .padding(10.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 8 * density
            }
            .clickable { rotated = !rotated },
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = colors.containerColor),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            if (!rotated) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    val (icon, brand, number, holder, date) = createRefs()
                    background?.apply {
                        Image(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    alpha = contentAlpha
                                },
                            contentScale = ContentScale.FillBounds,
                            painter = painterResource(this),
                            contentDescription = null
                        )
                    }
                    Icon(
                        painter = painterResource(R.drawable.ic_nfc_symbol),
                        contentDescription = "CreditCard Icon",
                        modifier = Modifier
                            .wrapContentSize()
                            .graphicsLayer { alpha = contentAlpha }
                            .constrainAs(icon) {
                                start.linkTo(parent.start, margin = 10.dp)
                                top.linkTo(parent.top, margin = 10.dp)
                            },
                        tint = Color.White
                    )
                    Image(
                        painter = painterResource(id = bank.brand),
                        contentDescription = "CreditCard Brand",
                        modifier = Modifier
                            .wrapContentSize()
                            .graphicsLayer { alpha = contentAlpha }
                            .constrainAs(brand) {
                                end.linkTo(parent.end, margin = 10.dp)
                                top.linkTo(parent.top, margin = 10.dp)
                            }
                    )
                    Text(
                        text = bank.number,
                        color = colors.contentColor,
                        modifier = Modifier
                            .graphicsLayer { alpha = contentAlpha }
                            .constrainAs(number) {
                                start.linkTo(parent.start, margin = 10.dp)
                                top.linkTo(icon.bottom)
                                bottom.linkTo(holder.top)
                            },
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )
                    Text(
                        text = cardHolder,
                        color = colors.contentColor,
                        modifier = Modifier
                            .graphicsLayer { alpha = contentAlpha }
                            .constrainAs(holder) {
                                start.linkTo(parent.start, margin = 10.dp)
                                bottom.linkTo(parent.bottom, margin = 10.dp)
                            }
                    )
                    Text(
                        text = cardDate,
                        color = colors.contentColor,
                        modifier = Modifier
                            .graphicsLayer {
                                alpha = contentAlpha
                            }
                            .constrainAs(date) {
                                end.linkTo(parent.end, margin = 10.dp)
                                bottom.linkTo(parent.bottom, margin = 10.dp)
                            }
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                ) {
                    Divider(
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .graphicsLayer {
                                alpha = (1f - contentAlpha)
                            },
                        color = colors.magneticStripeColor,
                        thickness = 50.dp
                    )
                    Text(
                        text = bank.signature,
                        color = colors.onSignatureAreaColor,
                        modifier = Modifier
                            .padding(10.dp)
                            .background(colors.signatureAreaColor)
                            .fillMaxWidth()
                            .graphicsLayer {
                                alpha = (1f - contentAlpha)
                                rotationY = rotation
                            }
                            .padding(10.dp),
                        fontSize = 15.sp,
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = "Developed by VastGui",
                        color = colors.contentColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                alpha = (1f - contentAlpha)
                                rotationY = rotation
                            }
                            .padding(5.dp),
                        fontWeight = FontWeight.Thin,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}