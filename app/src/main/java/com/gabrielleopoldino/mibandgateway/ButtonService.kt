package com.gabrielleopoldino.mibandgateway

import android.app.Service
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.zhaoxiaodan.miband.ActionCallback
import com.zhaoxiaodan.miband.MiBand
import com.zhaoxiaodan.miband.model.VibrationMode
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.schedule

class ButtonService(val context: Context) {

    val miBand: MiBand
    val bluetoothManager: BluetoothManager
    val debounceTime : Long = 700 //ms
    var connected = false

    init {
        miBand = MiBand(context)
        bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

    fun scan(): List<BluetoothDevice> {
        return bluetoothManager.getConnectedDevices(BluetoothProfile.GATT_SERVER)
    }

    fun startAtFirstDevice(buttonListener: ButtonListener){
        val devices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT_SERVER)
        if (!devices.isEmpty())
            startAtDevice(devices[0], buttonListener)

    }

    fun startAtDevice(device: BluetoothDevice, buttonListener: ButtonListener){
        if (!connected)
            miBand.connect(device, object : ActionCallback {

                override fun onSuccess(data: Any?) {

                    miBand.startVibration(VibrationMode.VIBRATION_WITH_LED)
                    Log.d("Button Service", "connect success")
                    //Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show()
                    connected = true
                    buttonTask(buttonListener)

                    miBand.addDisconnectedListener {
                        connected = false
                        Log.d("Button Service", "Disconnected")

                    }
                }


                override fun onFail(errorCode: Int, msg: String) {
                    //Toast.makeText(context, "Connect Failed ($errorCode): $msg", Toast.LENGTH_SHORT).show()
                    Log.d("Button Service", "connect fail, code:$errorCode,mgs:$msg")
                }
            })
        else
            buttonTask(buttonListener)

    }

    fun addOnDisconnectListener( listener : () -> Unit) {
        miBand.addDisconnectedListener { listener }
    }

    fun clean() {
        this.miBand.clean()
    }

    fun buttonTask(buttonListener: ButtonListener) {
        val timesPressed = AtomicInteger(0)
        val timesBounced = AtomicInteger(0)
        val lastClick = AtomicLong(0)

        miBand.setOnButtonPress { data: ByteArray? ->
            if (data != null && data[0] == 4.toByte()) {
                timesPressed.incrementAndGet()
                lastClick.set(System.currentTimeMillis())

                Timer("debouceTask", false).schedule(debounceTime){
                    var p = timesPressed.get()
                    if (p  == timesBounced.incrementAndGet())
                    {
                        //Toast.makeText(context, "Button pressed $p times", Toast.LENGTH_SHORT).show()
                        Log.d("Button Service", "Button pressed $p times")
                        when (p) {
                            1 -> buttonListener.oneClick()
                            2 -> buttonListener.twoClick()
                            3 -> buttonListener.threeClick()
                        }
                        timesPressed.set(0)
                        timesBounced.set(0)
                    }
                }
            }
        }
    }
}