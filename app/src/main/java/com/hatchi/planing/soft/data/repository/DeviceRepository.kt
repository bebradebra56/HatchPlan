package com.hatchi.planing.soft.data.repository

import com.hatchi.planing.soft.data.dao.DeviceDao
import com.hatchi.planing.soft.data.entity.Device
import kotlinx.coroutines.flow.Flow

class DeviceRepository(private val deviceDao: DeviceDao) {
    fun getAllDevices(): Flow<List<Device>> = deviceDao.getAllDevices()

    fun getActiveDevices(): Flow<List<Device>> = deviceDao.getActiveDevices()

    suspend fun getDeviceById(id: Long): Device? = deviceDao.getDeviceById(id)

    suspend fun insertDevice(device: Device): Long = deviceDao.insertDevice(device)

    suspend fun updateDevice(device: Device) = deviceDao.updateDevice(device)

    suspend fun deleteDevice(device: Device) = deviceDao.deleteDevice(device)
}

