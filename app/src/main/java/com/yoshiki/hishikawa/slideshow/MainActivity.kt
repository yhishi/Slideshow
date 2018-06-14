package com.yoshiki.hishikawa.slideshow

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import android.widget.ViewSwitcher
import kotlinx.android.synthetic.main.activity_main.*
import android.R.attr.button
import android.view.View
import android.R.id.button1
import android.media.MediaPlayer
import android.util.Log
import com.yoshiki.hishikawa.slideshow.R.id.imageSwitcher
import android.os.Handler
import kotlin.concurrent.timer


class MainActivity : AppCompatActivity() {

    /* parametor */
    private val resources = listOf(
            R.drawable.slide00, R.drawable.slide01,
            R.drawable.slide02, R.drawable.slide03,
            R.drawable.slide04, R.drawable.slide05,
            R.drawable.slide06, R.drawable.slide07,
            R.drawable.slide08, R.drawable.slide09, R.drawable.slide10
    )
    // 表示中のimage画像記憶用の変数
    private var position = 0
    private var isSlideshow = false
    private val handler = Handler()
    private lateinit var player: MediaPlayer

    /* method */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ビューを切り替えるためのファクトリクラスの指定 （Kotlin SAM変換使用）
        imageSwitcher.setFactory{
            // イメージビューのインスタンス生成 （ラムダ式 生成したビューがSAMインターフェースのmakeViewメソッドの戻り値）
            ImageView(this)
        }
        // 最初の画像を表示
        imageSwitcher.setImageResource(resources[0])

        prevButton.setOnClickListener {
            imageSwitcher.setInAnimation(this, android.R.anim.fade_in)
            imageSwitcher.setOutAnimation(this, android.R.anim.fade_out)
            movePosition(-1)
        }
        nextButton.setOnClickListener {
            imageSwitcher.setInAnimation(this, android.R.anim.slide_in_left)
            imageSwitcher.setOutAnimation(this, android.R.anim.slide_out_right)
            movePosition(1)
        }
        // 3秒おきに画像を入れ替える（タイマー関数は別スレッドで処理される）
        timer(period = 3000) {
            // 画面処理なのでメインスレッドで実施（handlerはメインスレッドで生成したため、メインスレッドで動く）
            handler.post {
                if(isSlideshow) movePosition(1)
            }
        }
        // スライドショーボタンでOn,Offを切り替える
        slideshowButton.setOnClickListener {
            isSlideshow = !isSlideshow

            when(isSlideshow) {
                true -> player.start()  // サウンド再生
                false -> player.apply {
                    pause()             // サウンド一時停止
                    seekTo(0)     // サウンドを先頭に戻す
                }
            }
        }

        // MediaPlayerインスタンス生成
        player = MediaPlayer.create(this, R.raw.getdown)

        // サウンドの繰り返し再生設定
        player.isLooping = true

    }

    // 画像変更
    private fun movePosition(move: Int) {
        position += move

        // 画像枚数を超えた場合、最初の画像
        if(position >= resources.size) {
            position = 0
        } else if(position < 0) {
            position = resources.size - 1
        }
        imageSwitcher.setImageResource(resources[position])
    }
}
