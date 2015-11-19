package rosedmonton.camera;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import org.ros.android.RosActivity;
import org.ros.android.view.camera.RosCameraPreviewView;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import android.util.Log;
import java.io.IOException;

/**
 * Code modified from org.ros.android.android_tutorial_camera.MainActivity
 * Original authors:
 * @author ethan.rublee@gmail.com (Ethan Rublee)
 * @author damonkohler@google.com (Damon Kohler)
 */
public class CameraActivity extends RosActivity
{
    private int cameraId;
    private RosCameraPreviewView rosCameraPreviewView;

    public CameraActivity() {
        super("CameraActivity", "CameraActivity");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.main);
        rosCameraPreviewView = (RosCameraPreviewView) findViewById(R.id.ros_camera_preview_view);
        Button controllerButton = (Button) findViewById(R.id.button);
        controllerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });
    }

    public void switchCamera(){
        int numberOfCameras = Camera.getNumberOfCameras();
        final Toast toast;
        if (numberOfCameras > 1) {
            cameraId = (cameraId + 1) % numberOfCameras;
            rosCameraPreviewView.releaseCamera();
            rosCameraPreviewView.setCamera(Camera.open(cameraId));
            toast = Toast.makeText(this, "Switching cameras.", Toast.LENGTH_SHORT);
        } else {
            toast = Toast.makeText(this, "No alternative cameras to switch to.", Toast.LENGTH_SHORT);
        }
        runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toast.show();
                }
            });
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        cameraId = 0;

        rosCameraPreviewView.setCamera(Camera.open(cameraId));
        try {
            java.net.Socket socket = new java.net.Socket(getMasterUri().getHost(), getMasterUri().getPort());
            java.net.InetAddress local_network_address = socket.getLocalAddress();
            socket.close();
            NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(local_network_address.getHostAddress(), getMasterUri());
            nodeMainExecutor.execute(rosCameraPreviewView, nodeConfiguration);
        } catch (IOException e) {
            // Socket problem
            Log.e("Camera Tutorial", "socket error trying to get networking information from the master uri");
        }

    }
}
