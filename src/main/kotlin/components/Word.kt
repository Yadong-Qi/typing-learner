package components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import data.Word
import kotlinx.coroutines.launch
import player.AudioButton
import state.GlobalState
import state.getResourcesFile
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.DataLine
import javax.sound.sampled.FloatControl

/** 单词组件
 * @param word 单词
 * @param global 全局状态
 * @param wordVisible 单词可见性
 * @param pronunciation 单词发音
 * @param isDictation 是否是听写模式
 * @param isReviewWrongList 是否是复习错误单词模式
 * @param correctTime 单词的正确数
 * @param wrongTime 单词的错误数
 * @param textFieldValue 用户输入的字符串
 * @param typingResult 用户输入字符的结果
 * @param checkTyping 检查用户的输入是否正确的回调
 */
@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun Word(
    word: Word,
    global: GlobalState,
    isDictation:Boolean,
    wordVisible:Boolean,
    pronunciation: String,
    isReviewWrongList:Boolean,
    fontFamily: FontFamily,
    audioPath: String,
    correctTime: Int,
    wrongTime: Int,
    textFieldValue: String,
    typingResult: List<Pair<Char, Boolean>>,
    checkTyping: (String) -> Unit,
    focusRequester: FocusRequester,
    textFieldKeyEvent: (KeyEvent) -> Boolean,
    showMenu: () -> Unit,
) {

    /**
     * 协程构建器
     */
    val scope = rememberCoroutineScope()

    val wordValue = word.value
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 48.dp).height(IntrinsicSize.Max)
        ) {
            var textHeight by remember { mutableStateOf(0.dp) }
            val bottom = computeBottom(
               textStyle =  global.textStyle,
                textHeight = textHeight,
            )
            val smallStyleList = listOf("H5","H6","Subtitle1","Subtitle2","Body1","Body2","Button","Caption","Overline")
            Box(Modifier
                .width(intrinsicSize = IntrinsicSize.Max)
                .height(intrinsicSize = IntrinsicSize.Max)
                .padding(start = 50.dp)
                .onPointerEvent(PointerEventType.Enter) {
                    if (!isDictation || (isReviewWrongList)) {
                        showMenu()
                    }
                }) {
                val fontSize = global.fontSize
                CompositionLocalProvider(
                    LocalTextInputService provides null
                ) {
                    BasicTextField(
                        value = textFieldValue,
                        onValueChange = { input ->
                            scope.launch {
                                checkTyping(input)
                            }
                        },
                        singleLine = true,
                        cursorBrush = SolidColor(MaterialTheme.colors.primary),
                        textStyle = TextStyle(
                            color = Color.Transparent,
                            fontSize = fontSize,
                            letterSpacing =  global.letterSpacing,
                            fontFamily =fontFamily
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = bottom)
                            .align(Alignment.Center)
                            .focusRequester(focusRequester)
                            .onKeyEvent { textFieldKeyEvent(it) }
                    )
                }
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
                Text(
                    modifier = Modifier
                        .padding(bottom = bottom)
                        .align(Alignment.Center)
                        .onGloballyPositioned { layoutCoordinates ->
                        textHeight = (layoutCoordinates.size.height).dp
                    },
                    text = buildAnnotatedString {
                        typingResult.forEach { (char, correct) ->
                            if (correct) {
                                withStyle(
                                    style = SpanStyle(
                                        color = MaterialTheme.colors.primary,
                                        fontSize = fontSize,
                                        letterSpacing = global.letterSpacing,
                                        fontFamily = fontFamily,
                                    )
                                ) {
                                    append(char)
                                }
                            } else {
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.Red,
                                        fontSize =fontSize,
                                        letterSpacing = global.letterSpacing,
                                        fontFamily =fontFamily,
                                    )
                                ) {
                                    if (char == ' ') {
                                        append("_")
                                    } else {
                                        append(char)
                                    }
                                }
                            }
                        }
                        val remainChars = wordValue.substring(typingResult.size)
                        if (isDictation && !isReviewWrongList) {
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colors.onBackground,
                                    fontSize = fontSize,
                                    letterSpacing =  global.letterSpacing,
                                    fontFamily = fontFamily,
                                )
                            ) {
                                repeat(remainChars.length) {
                                    append(" ")
                                }

                            }
                        } else {
                            if (wordVisible) {
                                withStyle(
                                    style = SpanStyle(
                                        color = MaterialTheme.colors.onBackground,
                                        fontSize = fontSize,
                                        letterSpacing =  global.letterSpacing,
                                        fontFamily =fontFamily,
                                    )
                                ) {
                                    append(remainChars)
                                }
                            } else {
                                withStyle(
                                    style = SpanStyle(
                                        color = MaterialTheme.colors.onBackground,
                                        fontSize = fontSize,
                                        letterSpacing =  global.letterSpacing,
                                        fontFamily = fontFamily,
                                    )
                                ) {
                                    repeat(remainChars.length) {
                                        append("_")
                                    }
                                }

                            }

                        }
                    }
                )
            }


            Column {
                val top = (textHeight - 36.dp).div(2)
                var numberFontSize = LocalTextStyle.current.fontSize
                if(smallStyleList.contains(global.textStyle)) numberFontSize = MaterialTheme.typography.overline.fontSize
                Spacer(modifier = Modifier.height(top))
                Text(text = "${if (correctTime > 0) correctTime else ""}",
                    color = MaterialTheme.colors.primary,
                    fontSize =  numberFontSize)
                Spacer(modifier = Modifier.height(top))
                Text(text = "${if (wrongTime > 0) wrongTime else ""}",
                    color = Color.Red,
                    fontSize =  numberFontSize
                )
            }
            var paddingTop = textHeight.div(2) - 20.dp
            if(paddingTop<0.dp) paddingTop =  0.dp
            if(global.textStyle == "H1") paddingTop = 23.dp

            AudioButton(
                audioPath = audioPath,
                volume = global.audioVolume,
                pronunciation = pronunciation,
                paddingTop = paddingTop,
            )
        }
}

/**
 * 音标组件
 */
@Composable
fun Phonetic(
    word: Word,
    phoneticVisible: Boolean,
) {
    if (phoneticVisible) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (word.usphone.isNotEmpty()) {
                SelectionContainer {
                    Text(
                        text = "美:${word.usphone}",
                        color = MaterialTheme.colors.onBackground,
                        modifier = Modifier.padding(start = 5.dp, end = 5.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(5.dp))
            if (word.ukphone.isNotEmpty()) {
                SelectionContainer {
                    Text(
                        text = "英:${word.ukphone}", color = MaterialTheme.colors.onBackground,
                        modifier = Modifier.padding(start = 5.dp, end = 5.dp)
                    )
                }
            }

        }
    }
}


/**
 * 确认删除对话框
 * @param message 要显示的消息
 * @param confirm 点击确认之后调用的回调
 * @param close 点击取消之后调用的回调
 */
@ExperimentalComposeUiApi
@Composable
fun ConfirmationDelete(message: String, confirm: () -> Unit, close: () -> Unit) {
    Dialog(
        title = "删除",
        onCloseRequest = { close() },
        undecorated = true,
        resizable = false,
        state = rememberDialogState(
            position = WindowPosition(Alignment.Center),
            size = DpSize(400.dp, 300.dp)
        ),
    ) {
        Surface(
            elevation = 5.dp,
            shape = RectangleShape,
            border = BorderStroke(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.12f))
        ) {
            val focusRequester = remember { FocusRequester() }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
                    .background(MaterialTheme.colors.background)
                    .focusRequester(focusRequester)
                    .onKeyEvent { keyEvent ->
                        if(keyEvent.key == Key.Y && keyEvent.type == KeyEventType.KeyUp){
                            confirm()
                            true
                        }else if((keyEvent.key == Key.N || keyEvent.key == Key.Escape )&& keyEvent.type == KeyEventType.KeyUp){
                            close()
                            true
                        }else false
                    }
            ) {
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp)
                ) {
                    Text(message)
                }

                Spacer(Modifier.height(20.dp))
                Row {
                    OutlinedButton(onClick = { confirm() }) {
                        Text("确定(Y)")
                    }
                    Spacer(Modifier.width(10.dp))
                    OutlinedButton(onClick = { close() }) {
                        Text("取消(N)")
                    }
                }
            }
        }
    }
}

/**
 * 播放音效
 * @param path 路径
 * @param volume 音量
 */
fun playSound(path: String, volume: Float) {
    try {
        val file = getResourcesFile(path)
        AudioSystem.getAudioInputStream(file).use { audioStream ->
            val format = audioStream.format
            val info: DataLine.Info = DataLine.Info(Clip::class.java, format)
            val clip: Clip = AudioSystem.getLine(info) as Clip
            clip.open(audioStream)
            val gainControl = clip.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
            val range = gainControl.maximum - gainControl.minimum
            val value = (range * volume) + gainControl.minimum
            gainControl.value = value
            clip.start()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/** 计算单词的底部内边距 */
fun computeBottom(
    textStyle:String,
    textHeight:Dp,
): Dp {
    var bottom = 0.dp
    val smallStyleList = listOf("H5","H6","Subtitle1","Subtitle2","Body1","Body2","Button","Caption","Overline")
    if(smallStyleList.contains(textStyle)) bottom = (36.dp - textHeight).div(2)
    if(bottom<0.dp) bottom = 0.dp
    if(bottom>7.5.dp) bottom = 5.dp
    return bottom
}