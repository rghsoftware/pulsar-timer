package com.rghsoftware.pulsartimer

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.os.Build
import android.os.ParcelUuid
import java.util.UUID

val PULSAR_TIMER_SERVICE_UUID = UUID.fromString("3b86824f-13a6-4c3f-8de1-23d4770db65d")
val TIMER_STATUS_UUID = UUID.fromString("ba7c88f0-808d-442b-a0b6-ce7a5fe614dc")
val TIMER_CONTROL_UUID = UUID.fromString("6c2e882b-d6f9-426a-91e3-61e8112dc716")

// IMPORTANT: This class is not production-ready and is a work in progress.
@SuppressLint("MissingPermission") // TODO: Handle permissions properly
class TimerGattServer(
    private val context: Context,
    private val onCommandReceived: (Byte) -> Unit
) {
    private val bluetoothManager: BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private var gattServer: android.bluetooth.BluetoothGattServer? = null
    private var connectedDevice: BluetoothDevice? = null

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            super.onStartSuccess(settingsInEffect)
        }

        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
        }
    }

    private val gattServerCallback = object : BluetoothGattServerCallback() {
        override fun onConnectionStateChange(device: BluetoothDevice?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                connectedDevice = device
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                connectedDevice = null
            }
        }

        override fun onCharacteristicWriteRequest(
            device: BluetoothDevice?,
            requestId: Int,
            characteristic: BluetoothGattCharacteristic?,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray?
        ) {
            if (characteristic?.uuid == TIMER_CONTROL_UUID) {
                value?.firstOrNull()?.let { command ->
                    onCommandReceived(command)
                }
                if (responseNeeded) {
                    gattServer?.sendResponse(
                        device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        0,
                        null
                    )
                }
            }
        }
    }

    fun startServer() {
        gattServer = bluetoothManager.openGattServer(context, gattServerCallback)
        setupGattService()
        startAdvertising()
    }

    fun stopServer() {
        bluetoothManager.adapter.bluetoothLeAdvertiser.stopAdvertising(advertiseCallback)
        gattServer?.close()
        gattServer = null
        connectedDevice = null
    }

    /**
     * Updates the timer status and notifies the connected client.
     * This function handles the deprecated `characteristic.setValue()` by checking the
     * Android version and using the appropriate method.
     */
    fun updateStatus(sessionTitle: String, timeDisplay: String) {
        val characteristic = gattServer?.getService(PULSAR_TIMER_SERVICE_UUID)
            ?.getCharacteristic(TIMER_STATUS_UUID) ?: return
        val fullStatusString = "$sessionTitle:$timeDisplay"
        val value = fullStatusString.toByteArray(Charsets.UTF_8)

        connectedDevice?.let { device ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                gattServer?.notifyCharacteristicChanged(device, characteristic, false, value)
            } else {
                characteristic.value = value
                gattServer?.notifyCharacteristicChanged(device, characteristic, false)
            }
        }
    }

    private fun setupGattService() {
        val service = BluetoothGattService(
            PULSAR_TIMER_SERVICE_UUID,
            BluetoothGattService.SERVICE_TYPE_PRIMARY
        )

        // Timer Status: Phone -> Watch
        val timerStatusCharacteristic = BluetoothGattCharacteristic(
            TIMER_STATUS_UUID,
            BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            BluetoothGattCharacteristic.PERMISSION_READ
        )

        // Timer Control: Watch -> Phone
        val timerControlCharacteristic = BluetoothGattCharacteristic(
            TIMER_CONTROL_UUID,
            BluetoothGattCharacteristic.PROPERTY_WRITE,
            BluetoothGattCharacteristic.PERMISSION_WRITE
        )

        service.addCharacteristic(timerStatusCharacteristic)
        service.addCharacteristic(timerControlCharacteristic)
        gattServer?.addService(service)
    }

    private fun startAdvertising() {
        val advertiser = bluetoothManager.adapter.bluetoothLeAdvertiser
        val settings = AdvertiseSettings.Builder().build()
        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(false)
            .addServiceUuid(ParcelUuid(PULSAR_TIMER_SERVICE_UUID))
            .build()

        advertiser.startAdvertising(settings, data, advertiseCallback)
    }


}