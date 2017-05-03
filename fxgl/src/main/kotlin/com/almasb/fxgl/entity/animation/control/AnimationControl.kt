/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.entity.animation.control

import com.almasb.fxgl.ecs.AbstractControl
import com.almasb.fxgl.ecs.Entity
import javafx.util.Duration

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class AnimationControl(val delay: Duration,
                                val duration: Duration,
                                val cycleCount: Int) : AbstractControl() {

    private var time = 0.0
    private var endTime = duration.toSeconds()

    private var count = 0

    private var checkDelay = true

    override fun onUpdate(entity: Entity, tpf: Double) {
        if (checkDelay) {
            time += tpf

            if (time >= delay.toSeconds()) {
                checkDelay = false
                time = 0.0
            } else {
                return
            }
        }

        if (time == 0.0) {
            onCycleStarted()

            onProgress(0.0)

            time += tpf
            return
        }

        time += tpf

        if (time >= endTime) {
            onCycleFinished()

            onProgress(1.0)

            count++

            if (count >= cycleCount) {
                onFinished()
                entity.removeControl(javaClass)
            } else {
                time = 0.0
            }

            return
        }

        onProgress(time / endTime)
    }

    /**
     * @param progress value in [0..1]
     */
    protected abstract fun onProgress(progress: Double)

    protected open fun onCycleStarted() {}

    protected open fun onCycleFinished() {}

    protected open fun onFinished() {}
}