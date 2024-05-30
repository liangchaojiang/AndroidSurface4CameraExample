package com.example.surfaces

import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.hardware.camera2.CameraManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.surfaces.helpers.ContextHelper
import com.example.surfaces.helpers.OpenGLScene
import com.example.surfaces.helpers.PermissionHelper
import com.example.surfaces.machines.*


class MainActivity : AppCompatActivity() {
    private lateinit var recordBtn: TextView
    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var glSurfaceView1: GLSurfaceView
    private lateinit var glSurfaceView2: GLSurfaceView
    private lateinit var glSurfaceView3: GLSurfaceView

    private val glSurfaceMachine = GLSurfaceMachine("0")
    private val glSurfaceMachine1 = GLSurfaceMachine("1")
    private val glSurfaceMachine2 = GLSurfaceMachine("2")
    private val glSurfaceMachine3 = GLSurfaceMachine("3")
    private val cameraMachine = CameraMachine("0")
    private val cameraMachine1 = CameraMachine("1")
    private val cameraMachine2 = CameraMachine("2")
    private val cameraMachine3 = CameraMachine("3")
    private val encoderMachine = EncoderMachine("0")
    private val encoderMachine1 = EncoderMachine("1")
    private val encoderMachine2 = EncoderMachine("2")
    private val encoderMachine3 = EncoderMachine("3")

    private val permission = PermissionHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gl_layout_activity)

        recordBtn = findViewById(R.id.record_btn)
        glSurfaceView = findViewById(R.id.gl_view)
        glSurfaceView1 = findViewById(R.id.gl_view1)
        glSurfaceView2 = findViewById(R.id.gl_view2)
        glSurfaceView3 = findViewById(R.id.gl_view3)

        val width = ContextHelper.getDisplayWidth()/8
        glSurfaceView.layoutParams.width = (OpenGLScene.ASPECT_RATIO * width).toInt()
        glSurfaceView.layoutParams.height = width

        glSurfaceView1.layoutParams.width = (OpenGLScene.ASPECT_RATIO * width).toInt()
        glSurfaceView1.layoutParams.height = width

        glSurfaceView2.layoutParams.width = (OpenGLScene.ASPECT_RATIO * width).toInt()
        glSurfaceView2.layoutParams.height = width

        glSurfaceView3.layoutParams.width = (OpenGLScene.ASPECT_RATIO * width).toInt()
        glSurfaceView3.layoutParams.height = width

        glSurfaceMachine.send(
            GLSurfaceAction.Create(
                glSurfaceView,
                CanvasDrawable(),
                encoderMachine.drawingCaller
            )
        )

        glSurfaceMachine1.send(
            GLSurfaceAction.Create(
                glSurfaceView1,
                CanvasDrawable(),
                encoderMachine1.drawingCaller
            )
        )
        glSurfaceMachine2.send(
            GLSurfaceAction.Create(
                glSurfaceView2,
                CanvasDrawable(),
                encoderMachine2.drawingCaller
            )
        )
        glSurfaceMachine3.send(
            GLSurfaceAction.Create(
                glSurfaceView3,
                CanvasDrawable(),
                encoderMachine3.drawingCaller
            )
        )

        encoderMachine.toggleRecordCallback = { isActive ->
            recordBtn.text = getString(
                if (isActive) R.string.record_stop
                else R.string.record_start
            )
        }

        encoderMachine1.toggleRecordCallback = { isActive ->
            recordBtn.text = getString(
                if (isActive) R.string.record_stop
                else R.string.record_start
            )
        }

        encoderMachine2.toggleRecordCallback = { isActive ->
            recordBtn.text = getString(
                if (isActive) R.string.record_stop
                else R.string.record_start
            )
        }

        encoderMachine3.toggleRecordCallback = { isActive ->
            recordBtn.text = getString(
                if (isActive) R.string.record_stop
                else R.string.record_start
            )
        }


        recordBtn.setOnClickListener {
            if (encoderMachine.isActive()) {
                encoderMachine.send(EncoderAction.Stop)
                encoderMachine1.send(EncoderAction.Stop)
                encoderMachine2.send(EncoderAction.Stop)
                encoderMachine3.send(EncoderAction.Stop)
            } else {
                permission.checkOrAsk(WRITE_EXTERNAL_STORAGE) {
                    encoderMachine.send(
                        EncoderAction.Start(
                            glSurfaceMachine.encoderProducer
                        )
                    )
                    encoderMachine1.send(
                        EncoderAction.Start(
                            glSurfaceMachine1.encoderProducer
                        )
                    )

                    encoderMachine2.send(
                        EncoderAction.Start(
                            glSurfaceMachine2.encoderProducer
                        )
                    )

                    encoderMachine3.send(
                        EncoderAction.Start(
                            glSurfaceMachine3.encoderProducer
                        )
                    )

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        glSurfaceMachine.send(GLSurfaceAction.Start)
        glSurfaceMachine1.send(GLSurfaceAction.Start)
        glSurfaceMachine2.send(GLSurfaceAction.Start)
        glSurfaceMachine3.send(GLSurfaceAction.Start)
        permission.checkOrAsk(CAMERA) {
            cameraMachine.send(CameraAction.Start(
                getSystemService(Context.CAMERA_SERVICE) as CameraManager,
                glSurfaceMachine.fullscreenProducer
            ))

            cameraMachine1.send(CameraAction.Start(
                getSystemService(Context.CAMERA_SERVICE) as CameraManager,
                glSurfaceMachine1.fullscreenProducer
            ))

            cameraMachine2.send(CameraAction.Start(
                getSystemService(Context.CAMERA_SERVICE) as CameraManager,
                glSurfaceMachine2.fullscreenProducer
            ))

            cameraMachine3.send(CameraAction.Start(
                getSystemService(Context.CAMERA_SERVICE) as CameraManager,
                glSurfaceMachine3.fullscreenProducer
            ))

        }
    }

    override fun onPause() {
        super.onPause()
        glSurfaceMachine.send(GLSurfaceAction.Stop)
        encoderMachine.send(EncoderAction.Stop)
        cameraMachine.send(CameraAction.Stop)

        glSurfaceMachine1.send(GLSurfaceAction.Stop)
        encoderMachine1.send(EncoderAction.Stop)
        cameraMachine1.send(CameraAction.Stop)

        glSurfaceMachine2.send(GLSurfaceAction.Stop)
        encoderMachine2.send(EncoderAction.Stop)
        cameraMachine2.send(CameraAction.Stop)

        glSurfaceMachine3.send(GLSurfaceAction.Stop)
        encoderMachine3.send(EncoderAction.Stop)
        cameraMachine3.send(CameraAction.Stop)


    }

    override fun onRequestPermissionsResult(
        reqCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(reqCode, permissions, grantResults)
        permission.bindTOonReqResult(reqCode, permissions, grantResults)
    }
}