package org.robolectric.shadows;

import static org.robolectric.shadow.api.Shadow.directlyOn;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.IBluetooth;
import android.content.Context;
import android.os.ParcelUuid;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;
import org.robolectric.util.ReflectionHelpers;

@Implements(BluetoothDevice.class)
public class ShadowBluetoothDevice {

  public static BluetoothDevice newInstance(String address) {
    return ReflectionHelpers.callConstructor(
        BluetoothDevice.class, ReflectionHelpers.ClassParameter.from(String.class, address));
  }

  @RealObject private BluetoothDevice realBluetoothDevice;
  private String name;
  private ParcelUuid[] uuids;

  /**
   * Implements getService() in the same way the original method does, but ignores any Exceptions
   * from invoking {@link BluetoothAdapter#getBluetoothService}.
   */
  @Implementation
  public static IBluetooth getService() {
    // Attempt to call the underlying getService method, but ignore any Exceptions. This allows us
    // to easily create BluetoothDevices for testing purposes without having any actual Bluetooth
    // capability.
    try {
      return directlyOn(BluetoothDevice.class, "getService");
    } catch (Exception e) {
      // No-op.
    }
    return null;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Implementation
  public String getName() {
    return name;
  }

  /** Sets the return value for {@link BluetoothDevice#getUuids}. */
  public void setUuids(ParcelUuid[] uuids) {
    this.uuids = uuids;
  }

  /**
   * Overrides behavior of {@link BluetoothDevice#getUuids} to return pre-set result.
   *
   * @returns Value set by calling {@link ShadowBluetoothDevice#setUuids}. If setUuids has not
   *     previously been called, will return null.
   */
  @Implementation
  protected ParcelUuid[] getUuids() {
    return uuids;
  }

  @Implementation
  protected BluetoothGatt connectGatt(
      Context context, boolean autoConnect, BluetoothGattCallback callback) {
    return ShadowBluetoothGatt.newInstance(realBluetoothDevice);
  }
}
