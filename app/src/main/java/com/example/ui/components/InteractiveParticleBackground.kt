package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.*
import kotlin.random.Random

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val radius: Float
) {
    val mass: Float = radius * radius // Mass proportional to radius squared
}

@Composable
fun InteractiveParticleBackground(
    modifier: Modifier = Modifier,
    particleColor: Color = MaterialTheme.colorScheme.primary,
    numParticles: Int = 40
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    var touchPosition by remember { mutableStateOf<Offset?>(null) }
    val particles = remember { mutableStateListOf<Particle>() }
    var time by remember { mutableStateOf(0f) }

    // Initialize particles
    LaunchedEffect(size) {
        if (size.width > 0 && size.height > 0 && particles.isEmpty()) {
            repeat(numParticles) {
                particles.add(
                    Particle(
                        x = Random.nextFloat() * size.width,
                        y = Random.nextFloat() * size.height,
                        vx = (Random.nextFloat() - 0.5f) * 4f,
                        vy = (Random.nextFloat() - 0.5f) * 4f,
                        radius = Random.nextFloat() * 4f + 2f
                    )
                )
            }
        }
    }

    // Animation Loop
    LaunchedEffect(Unit) {
        while (isActive) {
            val maxInteractionDist = min(size.width, size.height) * 0.4f
            val dampingRadius = maxInteractionDist * 0.3f

            for (i in particles.indices) {
                val p = particles[i]
                
                // Touch interaction
                touchPosition?.let { touch ->
                    val dx = touch.x - p.x
                    val dy = touch.y - p.y
                    val dist = sqrt(dx * dx + dy * dy)
                    
                    if (dist < maxInteractionDist && dist > 1f) {
                        val force = (maxInteractionDist - dist) / maxInteractionDist
                        // Radial gravity
                        p.vx += (dx / dist) * force * 0.2f
                        p.vy += (dy / dist) * force * 0.2f
                        
                        // Orbital force (Swirl)
                        p.vx += (-dy / dist) * force * 0.3f
                        p.vy += (dx / dist) * force * 0.3f
                        
                        // Inner damping
                        if (dist < dampingRadius) {
                            p.vx *= 0.95f
                            p.vy *= 0.95f
                        }
                    }
                }

                p.x += p.vx
                p.y += p.vy

                // Bounce off walls
                if (p.x < 0) { p.x = 0f; p.vx *= -1f }
                if (p.x > size.width) { p.x = size.width.toFloat(); p.vx *= -1f }
                if (p.y < 0) { p.y = 0f; p.vy *= -1f }
                if (p.y > size.height) { p.y = size.height.toFloat(); p.vy *= -1f }
            }

            // Collisions
            for (i in 0 until particles.size) {
                for (j in i + 1 until particles.size) {
                    val p1 = particles[i]
                    val p2 = particles[j]
                    val dx = p2.x - p1.x
                    val dy = p2.y - p1.y
                    val distSq = dx * dx + dy * dy
                    val radiusSum = p1.radius + p2.radius

                    if (distSq < radiusSum * radiusSum) {
                        val dist = sqrt(distSq)
                        if (dist == 0f) continue

                        // Resolve overlap
                        val overlap = radiusSum - dist
                        val nx = dx / dist
                        val ny = dy / dist
                        p1.x -= nx * overlap * 0.5f
                        p2.x += nx * overlap * 0.5f
                        p1.y -= ny * overlap * 0.5f
                        p2.y += ny * overlap * 0.5f

                        // Elastic collision
                        val rvx = p2.vx - p1.vx
                        val rvy = p2.vy - p1.vy
                        val velAlongNormal = rvx * nx + rvy * ny

                        if (velAlongNormal > 0) continue

                        val restitution = 0.85f
                        val jImpulse = -(1 + restitution) * velAlongNormal
                        val impulse = jImpulse / (1 / p1.mass + 1 / p2.mass)

                        val impulseX = nx * impulse
                        val impulseY = ny * impulse

                        p1.vx -= impulseX / p1.mass
                        p2.vx += impulseX / p2.mass
                        p1.vy -= impulseY / p1.mass
                        p2.vy += impulseY / p2.mass
                    }
                }
            }

            // Limit speed
            for (p in particles) {
                val speed = sqrt(p.vx * p.vx + p.vy * p.vy)
                if (speed > 8f) {
                    p.vx = (p.vx / speed) * 8f
                    p.vy = (p.vy / speed) * 8f
                }
            }

            time += 0.05f
            delay(16L) // Standard reliable delay
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size = it }
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    touchPosition = down.position
                    do {
                        val event = awaitPointerEvent()
                        val anyPressed = event.changes.any { it.pressed }
                        if (anyPressed) {
                            val pointer = event.changes.firstOrNull { it.pressed }
                            if (pointer != null) {
                                touchPosition = pointer.position
                            }
                        } else {
                            touchPosition = null
                        }
                    } while (event.changes.any { it.pressed })
                    touchPosition = null
                }
            }
    ) {
        val currentTime = time // Force redraw on state change
        val centerOffset = Offset(size.width / 2f, size.height * 0.4f)
        drawRect(
            brush = androidx.compose.ui.graphics.Brush.radialGradient(
                colors = listOf(Color(0xFF1E293B), Color(0xFF050608)),
                center = centerOffset,
                radius = size.width.coerceAtLeast(size.height) * 0.7f
            )
        )

        val maxDist = 150f

        // Draw connections
        for (i in 0 until particles.size) {
            for (j in i + 1 until particles.size) {
                val p1 = particles[i]
                val p2 = particles[j]
                val dx = p1.x - p2.x
                val dy = p1.y - p2.y
                val dist = sqrt(dx * dx + dy * dy)

                if (dist < maxDist) {
                    val strength = 1f - (dist / maxDist)
                    drawLine(
                        color = particleColor.copy(alpha = strength * 0.3f),
                        start = Offset(p1.x, p1.y),
                        end = Offset(p2.x, p2.y),
                        strokeWidth = strength * 2f
                    )
                }
            }
        }

        // Draw particles with triple glow
        for (p in particles) {
            // Outer glow
            drawCircle(
                color = particleColor.copy(alpha = 0.15f),
                radius = p.radius * 1.8f,
                center = Offset(p.x, p.y)
            )
            // Mid glow
            drawCircle(
                color = particleColor.copy(alpha = 0.45f),
                radius = p.radius,
                center = Offset(p.x, p.y)
            )
            // Core
            drawCircle(
                color = particleColor.copy(alpha = 0.95f),
                radius = p.radius * 0.5f,
                center = Offset(p.x, p.y)
            )
        }

        // Draw touch magnet glow
        touchPosition?.let { touch ->
            for (i in 1..3) {
                val phase = time + (i * PI.toFloat() / 1.5f)
                val radius = 30f + sin(phase) * 10f
                val alpha = (0.5f + sin(phase) * 0.3f).coerceIn(0f, 1f)
                drawCircle(
                    color = particleColor.copy(alpha = alpha),
                    radius = radius,
                    center = touch,
                    style = Stroke(width = 3f)
                )
            }
        }
    }
}
