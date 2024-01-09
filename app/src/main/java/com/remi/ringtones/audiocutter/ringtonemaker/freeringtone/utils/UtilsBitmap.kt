package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.MotionEventCompat
import androidx.core.view.ViewCompat
import androidx.exifinterface.media.ExifInterface
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.callback.ICallBackItem
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions.modifyOrientation
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.math.abs
import kotlin.math.roundToInt


object UtilsBitmap {

    fun loadBitmapFromView(view: View): Bitmap? {
        val b = Bitmap.createBitmap(view.layoutParams.width, view.layoutParams.height, Bitmap.Config.ARGB_8888)
        view.layout(view.left, view.top, view.right, view.bottom)
        val c = Canvas(b)
        c.rotate(view.rotation, view.width.toFloat() / 2, view.height.toFloat() / 2)
        view.draw(c)
        return b
    }

    fun drawIconWithPath(canvas: Canvas, path: Path, paint: Paint?, size: Float, x: Int, y: Int) {
        val rectF = RectF()
        path.computeBounds(rectF, true)
        val scale = if (rectF.width() >= rectF.height()) size / rectF.width()
        else size / rectF.height()
        canvas.translate(x.toFloat(), y.toFloat())
        canvas.scale(scale, scale)
        canvas.drawPath(path, paint!!)
    }

    fun toRGBString(color: Int): String {
        // format: #RRGGBB
        var red = Integer.toHexString(Color.red(color))
        var green = Integer.toHexString(Color.green(color))
        var blue = Integer.toHexString(Color.blue(color))
        if (red.length == 1) red = "0$red"
        if (green.length == 1) green = "0$green"
        if (blue.length == 1) blue = "0$blue"
        return "#$red$green$blue"
    }

    fun toARGBString(alpha: Int, color: Int): String {
        // format: #AARRGGBB
        var hex = Integer.toHexString(alpha).uppercase()
        var red = Integer.toHexString(Color.red(color))
        var green = Integer.toHexString(Color.green(color))
        var blue = Integer.toHexString(Color.blue(color))
        if (hex.length == 1) hex = "0$hex"
        if (red.length == 1) red = "0$red"
        if (green.length == 1) green = "0$green"
        if (blue.length == 1) blue = "0$blue"
        return "#$hex$red$green$blue"
    }

    fun drawableToBitmap(context: Context, width: Int, height: Int, drawableId: Int): Bitmap? {
        if (width <= 0 || height <= 0 || drawableId == 0) return null
        return drawableToBitmap(width, height, ContextCompat.getDrawable(context, drawableId))
    }

    private fun drawableToBitmap(width: Int, height: Int, drawable: Drawable?): Bitmap? {
        var bitmap: Bitmap? = null
        if (drawable == null) return null
        try {
            if (drawable is BitmapDrawable) {
                bitmap = drawable.bitmap
                if (bitmap != null && bitmap.height > 0) {
                    val matrix = Matrix()
                    val scaleWidth = width * 1.0f / bitmap.width
                    val scaleHeight = height * 1.0f / bitmap.height
                    matrix.postScale(scaleWidth, scaleHeight)
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                    return bitmap
                }
            }
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.apply {
                setBounds(0, 0, canvas.width, canvas.height)
                draw(canvas)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return bitmap
    }


    fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        drawable?.let {
            val bitmap = Bitmap.createBitmap(it.intrinsicWidth, it.intrinsicHeight, Bitmap.Config.ARGB_8888)
            if (bitmap != null) {
                val canvas = Canvas(bitmap)
                it.apply {
                    setBounds(0, 0, canvas.width, canvas.height)
                    draw(canvas)
                }
                return bitmap
            }
        }
        return null
    }

    fun getImageSize(context: Context, uri: Uri?): FloatArray {
        try {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            val input = context.contentResolver.openInputStream(uri!!)
            BitmapFactory.decodeStream(input, null, options)
            input!!.close()
            return floatArrayOf(options.outWidth.toFloat(), options.outHeight.toFloat())
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }
        return floatArrayOf(0f, 0f)
    }

    fun createByteImage(context: Context, uriPic: String?, callBack: ICallBackItem) {
        val bmp: Bitmap
        try {
            bmp = getBitmapFromUri(context, Uri.parse(uriPic))!!.modifyOrientation(context, Uri.parse(uriPic))
            val stream = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 84, stream)
            callBack.callBack(stream.toByteArray(), -1)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun createByteAudio(uriAudio: String?, callBack: ICallBackItem) {
        try {
            val inputStream = FileInputStream(uriAudio)
            val os = ByteArrayOutputStream()
            val buffer = ByteArray(0xFFFF)
            var len = inputStream.read(buffer)
            while (len != -1) {
                os.write(buffer, 0, len)
                len = inputStream.read(buffer)
            }
            callBack.callBack(os.toByteArray(), -1)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun saveBitmapToApp(
        context: Context?,
        bitmap: Bitmap,
        nameFolder: String,
        nameFile: String
    ): String {
        val directory = Utils.getStore(context!!) + "/" + nameFolder + "/"
        val myPath = File(directory, "$nameFile.png")
        try {
            val fos = FileOutputStream(myPath)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.close()
            return myPath.path
        } catch (e: Exception) {
            Log.e("SAVE_IMAGE", e.message, e)
        }
        return ""
    }

    fun getBitmapFromUri(context: Context, selectedFileUri: Uri?): Bitmap? {
        var image: Bitmap? = null
        try {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(selectedFileUri!!, "r")
            val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
            image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }

    fun getBitmapFromAsset(context: Context, nameFolder: String, name: String): Bitmap? {
        val istr: InputStream
        var bitmap: Bitmap? = null
        try {
            istr = context.assets.open("$nameFolder/$name")
            bitmap = BitmapFactory.decodeStream(istr)
        } catch (e: IOException) {
            // handle exception
            e.printStackTrace()
        }
        return bitmap
    }

    @SuppressLint("Recycle")
    @Throws(IOException::class)
    fun modifyOrientation(context: Context, bitmap: Bitmap, uri: Uri?): Bitmap {
        val `is` = context.contentResolver.openInputStream(uri!!)
        val ei = ExifInterface(`is`!!)
        return when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
            else -> bitmap
        }
    }

    @Throws(IOException::class)
    fun modifyOrientation(bitmap: Bitmap, fileDescriptor: FileDescriptor?): Bitmap {
        val ei = ExifInterface(fileDescriptor!!)
        return when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
            else -> bitmap
        }
    }

    fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    fun createFlippedBitmap(source: Bitmap, xFlip: Boolean, yFlip: Boolean): Bitmap {
        val matrix = Matrix()
        matrix.postScale(if (xFlip) -1F else 1F, if (yFlip) -1F else 1F, source.width / 2f, source.height / 2f)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    fun cropBitmapTransparency(bitmap: Bitmap): Bitmap? {
        var width = bitmap.width
        var height = bitmap.height
        var i = 0
        var i2 = 0
        while (true) {
            if (i2 >= bitmap.width) {
                i2 = 0
                break
            } else if (Color.alpha(bitmap.getPixel(i2, bitmap.height / 2)) > 0) break
            else i2++
        }
        var i3 = 0
        while (true) {
            if (i3 >= bitmap.height) break
            else if (Color.alpha(bitmap.getPixel(bitmap.width / 2, i3)) > 0) {
                i = i3
                break
            } else i3++
        }
        var width2 = bitmap.width - 1
        while (true) {
            if (width2 < 0) break
            else if (Color.alpha(bitmap.getPixel(width2, bitmap.height / 2)) > 0) {
                width = width2
                break
            } else width2--
        }
        var height2 = bitmap.height - 1
        while (true) {
            if (height2 < 0) break
            else if (Color.alpha(bitmap.getPixel(bitmap.width / 2, height2)) > 0) {
                height = height2
                break
            } else height2--
        }
        return if (width < i2 || height < i) null
        else Bitmap.createBitmap(bitmap, i2, i, width - i2, height - i)
    }

    fun fastBlur(bitmap: Bitmap, f: Float, i: Int): Bitmap? {
        var iArr: IntArray
        var i2 = i
        val createScaledBitmap =
            Bitmap.createScaledBitmap(bitmap, (bitmap.width * f).roundToInt(), (bitmap.height * f).roundToInt(), false)
        var copy = createScaledBitmap.copy(createScaledBitmap.config, true)
        if (i2 < 1) return null

        var width = copy.width
        var height = copy.height
        val i3 = width * height
        var iArr2 = IntArray(i3)
        copy.getPixels(iArr2, 0, width, 0, 0, width, height)
        val i4 = width - 1
        var i5 = height - 1
        var i6 = i2 + i2 + 1
        val iArr3 = IntArray(i3)
        val iArr4 = IntArray(i3)
        val iArr5 = IntArray(i3)
        var iArr6 = IntArray(width.coerceAtLeast(height))
        val i7 = i6 + 1 shr 1
        val i8 = i7 * i7
        val i9 = i8 * 256
        var iArr7 = IntArray(i9)
        for (i10 in 0 until i9) iArr7[i10] = i10 / i8
        val iArr8 = java.lang.reflect.Array.newInstance(Int::class.javaPrimitiveType, i6, 3) as Array<IntArray>
        val i11 = i2 + 1
        var i12 = 0
        var i13 = 0
        var i14 = 0
        while (i12 < height) {
            val bitmap2 = copy
            val i15 = height
            var i16 = 0
            var i17 = 0
            var i18 = 0
            var i19 = 0
            var i20 = 0
            var i21 = 0
            var i22 = 0
            var i23 = 0
            var i24 = -i2
            var i25 = 0
            while (i24 <= i2) {
                val i26 = i5
                val iArr9 = iArr6
                val i27 = iArr2[i13 + i4.coerceAtMost(i24.coerceAtLeast(0))]
                val iArr10 = iArr8[i24 + i2]
                iArr10[0] = i27 and 16711680 shr 16
                iArr10[1] = i27 and MotionEventCompat.ACTION_POINTER_INDEX_MASK shr 8
                iArr10[2] = i27 and 255
                val abs = i11 - abs(i24)
                i25 += iArr10[0] * abs
                i16 += iArr10[1] * abs
                i17 += iArr10[2] * abs
                if (i24 > 0) {
                    i21 += iArr10[0]
                    i22 += iArr10[1]
                    i23 += iArr10[2]
                } else {
                    i18 += iArr10[0]
                    i19 += iArr10[1]
                    i20 += iArr10[2]
                }
                i24++
                i5 = i26
                iArr6 = iArr9
            }
            val i28 = i5
            val iArr11 = iArr6
            var i29 = i2
            var i30 = i25
            var i31 = 0
            while (i31 < width) {
                iArr3[i13] = iArr7[i30]
                iArr4[i13] = iArr7[i16]
                iArr5[i13] = iArr7[i17]
                val i32 = i30 - i18
                val i33 = i16 - i19
                val i34 = i17 - i20
                val iArr12 = iArr8[(i29 - i2 + i6) % i6]
                val i35 = i18 - iArr12[0]
                val i36 = i19 - iArr12[1]
                val i37 = i20 - iArr12[2]
                if (i12 == 0) {
                    iArr = iArr7
                    iArr11[i31] = (i31 + i2 + 1).coerceAtMost(i4)
                } else iArr = iArr7

                val i38 = iArr2[i14 + iArr11[i31]]
                iArr12[0] = i38 and 16711680 shr 16
                iArr12[1] = i38 and MotionEventCompat.ACTION_POINTER_INDEX_MASK shr 8
                iArr12[2] = i38 and 255
                val i39 = i21 + iArr12[0]
                val i40 = i22 + iArr12[1]
                val i41 = i23 + iArr12[2]
                i30 = i32 + i39
                i16 = i33 + i40
                i17 = i34 + i41
                i29 = (i29 + 1) % i6
                val iArr13 = iArr8[i29 % i6]
                i18 = i35 + iArr13[0]
                i19 = i36 + iArr13[1]
                i20 = i37 + iArr13[2]
                i21 = i39 - iArr13[0]
                i22 = i40 - iArr13[1]
                i23 = i41 - iArr13[2]
                i13++
                i31++
                iArr7 = iArr
            }
            i14 += width
            i12++
            copy = bitmap2
            height = i15
            i5 = i28
            iArr6 = iArr11
        }
        val bitmap3 = copy
        val iArr14 = iArr7
        var i42 = i5
        val iArr15 = iArr6
        var i43 = height
        var i44 = 0
        while (i44 < width) {
            val i45 = -i2
            val i46 = i6
            val iArr16 = iArr2
            var i47 = 0
            var i48 = 0
            var i49 = 0
            var i50 = 0
            var i51 = 0
            var i52 = 0
            var i53 = 0
            var i54 = i45
            var i55 = i45 * width
            var i56 = 0
            var i57 = 0
            while (i54 <= i2) {
                val i58 = width
                val max = 0.coerceAtLeast(i55) + i44
                val iArr17 = iArr8[i54 + i2]
                iArr17[0] = iArr3[max]
                iArr17[1] = iArr4[max]
                iArr17[2] = iArr5[max]
                val abs2 = i11 - abs(i54)
                i56 += iArr3[max] * abs2
                i57 += iArr4[max] * abs2
                i47 += iArr5[max] * abs2
                if (i54 > 0) {
                    i51 += iArr17[0]
                    i52 += iArr17[1]
                    i53 += iArr17[2]
                } else {
                    i48 += iArr17[0]
                    i49 += iArr17[1]
                    i50 += iArr17[2]
                }
                val i59 = i42
                if (i54 < i59) i55 += i58
                i54++
                i42 = i59
                width = i58
            }
            val i60 = width
            val i61 = i42
            var i62 = i2
            var i63 = i44
            var i64 = i47
            val i65 = i43
            var i66 = i57
            var i67 = 0
            while (i67 < i65) {
                iArr16[i63] =
                    iArr16[i63] and ViewCompat.MEASURED_STATE_MASK or (iArr14[i56] shl 16) or (iArr14[i66] shl 8) or iArr14[i64]
                val i68 = i56 - i48
                val i69 = i66 - i49
                val i70 = i64 - i50
                val iArr18 = iArr8[(i62 - i2 + i46) % i46]
                val i71 = i48 - iArr18[0]
                val i72 = i49 - iArr18[1]
                val i73 = i50 - iArr18[2]
                if (i44 == 0) iArr15[i67] = (i67 + i11).coerceAtMost(i61) * i60

                val i74 = iArr15[i67] + i44
                iArr18[0] = iArr3[i74]
                iArr18[1] = iArr4[i74]
                iArr18[2] = iArr5[i74]
                val i75 = i51 + iArr18[0]
                val i76 = i52 + iArr18[1]
                val i77 = i53 + iArr18[2]
                i56 = i68 + i75
                i66 = i69 + i76
                i64 = i70 + i77
                i62 = (i62 + 1) % i46
                val iArr19 = iArr8[i62]
                i48 = i71 + iArr19[0]
                i49 = i72 + iArr19[1]
                i50 = i73 + iArr19[2]
                i51 = i75 - iArr19[0]
                i52 = i76 - iArr19[1]
                i53 = i77 - iArr19[2]
                i63 += i60
                i67++
                i2 = i
            }
            i44++
            i2 = i
            i42 = i61
            i43 = i65
            i6 = i46
            iArr2 = iArr16
            width = i60
        }
        val i78 = width
        bitmap3.setPixels(iArr2, 0, i78, 0, 0, i78, i43)
        return bitmap3
    }
}