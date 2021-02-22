package com.example.prk.santuy

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.prk.santuy.models.User
import kotlinx.android.synthetic.main.activity_show_qr.*
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import android.R.attr.y
import android.R.attr.x
import com.google.zxing.WriterException
import android.R.attr.bitmap
import android.R.attr.y
import android.R.attr.x
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.util.Log
import android.view.Display
import android.view.WindowManager
import com.example.prk.santuy.helper.Mode
import kotlinx.android.synthetic.main.fragment_account.*


class ShowQRActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        Mode.setTheme_(this)

        setContentView(R.layout.activity_show_qr)
        val user = intent.extras!!.get("user") as User

        val manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = manager.defaultDisplay
        val point = Point()
        display.getSize(point)
        val width = point.x
        val height = point.y
        val qrgEncoder: QRGEncoder

        var smallerDimension = if (width < height) width else height
        smallerDimension = smallerDimension * 3 / 4

        qrgEncoder = QRGEncoder(
                user.username, null,
                QRGContents.Type.TEXT,
                smallerDimension)
        try {

            val bitmap : Bitmap = qrgEncoder.encodeAsBitmap()

            userQRCode.setImageBitmap(bitmap)

        } catch (e: WriterException) {
            Log.v("NICKY", e.toString())
        }


        cQRCode.setOnClickListener {
            val intent =  Intent(this, MainActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
            finish()
        }

    }
}
