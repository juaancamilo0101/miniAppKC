const canvas = document.getElementById('game');
const ctx = canvas.getContext('2d');
function resize() { canvas.width = window.innerWidth; canvas.height = window.innerHeight; }
resize();
window.addEventListener('resize', resize);

const keys = new Set();
window.addEventListener('keydown', e => keys.add(e.key.toLowerCase()));
window.addEventListener('keyup', e => keys.delete(e.key.toLowerCase()));
let mouse = { x: 0, y: 0, down: false };
window.addEventListener('pointermove', e => { mouse.x = e.clientX; mouse.y = e.clientY; });
window.addEventListener('pointerdown', () => { mouse.down = true; });
window.addEventListener('pointerup', () => { mouse.down = false; });

const state = { scene: 'menu', t: 0, speed: 0.03, time: 0, section: 0, resolved: {},
  energy: 100, integridad: 100, capacitacion: 0, quimio: 0, resultado: null };

const path = [];
const eventos = [
  { id: 'ph_vaginal', section: 0, t: 0.08, titulo: 'pH vaginal ácido', opciones: [
      { txt: 'Refugiarse en microambientes alcalinos', ef: s => { s.integridad -= 4; s.energy = Math.max(0, s.energy - 5); } },
      { txt: 'Nadar rápido atravesando el ácido', ef: s => { s.integridad -= 12; s.energy = Math.max(0, s.energy - 10); s.speed += 0.005; } },
      { txt: 'Esperar mezcla con líquido seminal', ef: s => { s.time += 6; s.integridad -= 2; } }
    ] },
  { id: 'moco_cervical', section: 1, t: 0.22, titulo: 'Moco cervical', opciones: [
      { txt: 'Buscar canales bajos de moco', ef: s => { s.speed += 0.004; s.energy -= 6; } },
      { txt: 'Empujar directamente', ef: s => { s.integridad -= 15; } },
      { txt: 'Seguir ritmo de pulsos uterinos', ef: s => { s.speed += 0.006; } }
    ] },
  { id: 'defensas', section: 2, t: 0.40, titulo: 'Defensas celulares', opciones: [
      { txt: 'Evitar neutrófilos', ef: s => { s.integridad -= 5; s.energy -= 8; } },
      { txt: 'Camuflarse', ef: s => { s.integridad -= 2; s.energy -= 12; } },
      { txt: 'Ignorar', ef: s => { s.integridad -= 22; } }
    ] },
  { id: 'capacitacion', section: 3, t: 0.62, titulo: 'Capacitación espermática', opciones: [
      { txt: 'Intercambio con albúmina', ef: s => { s.capacitacion += 40; s.integridad -= 6; } },
      { txt: 'Activar bicarbonato', ef: s => { s.capacitacion += 35; s.energy -= 10; } },
      { txt: 'No capacitarse', ef: s => { s.capacitacion += 0; } }
    ] },
  { id: 'quimiotaxis', section: 4, t: 0.82, titulo: 'Señal de progesterona', opciones: [
      { txt: 'Seguir gradiente', ef: s => { s.quimio += 50; s.speed += 0.004; } },
      { txt: 'Seguir flujo', ef: s => { s.quimio += 15; } },
      { txt: 'Aleatorio', ef: s => { s.quimio += 0; s.integridad -= 8; } }
    ] }
];

function initPath() {
  path.length = 0;
  const W = canvas.width, H = canvas.height;
  path.push({ x: W * 0.5, y: H * 0.9 });
  path.push({ x: W * 0.48, y: H * 0.7 });
  path.push({ x: W * 0.55, y: H * 0.55 });
  path.push({ x: W * 0.60, y: H * 0.35 });
  path.push({ x: W * 0.72, y: H * 0.22 });
  path.push({ x: W * 0.85, y: H * 0.15 });
}

function lerp(a, b, t) { return a + (b - a) * t; }
function pointAt(t) {
  const n = path.length - 1;
  const idx = Math.min(n - 1, Math.floor(t * n));
  const frac = t * n - idx;
  const p = path[idx], q = path[idx + 1];
  return { x: lerp(p.x, q.x, frac), y: lerp(p.y, q.y, frac), angle: Math.atan2(q.y - p.y, q.x - p.x) };
}

function startGame() {
  state.scene = 'juego';
  state.t = 0;
  state.speed = 0.03;
  state.time = 0;
  state.section = 0;
  state.resuelto = {};
  state.energy = 100;
  state.integridad = 100;
  state.capacitacion = 0;
  state.quimio = 0;
  state.resultado = null;
}

function update(dt) {
  if (state.scene === 'menu') {
    if (keys.has(' ') || keys.has('enter')) { keys.clear(); startGame(); }
    return;
  }
  if (state.scene === 'evento') {
    if (keys.has('1') || keys.has('numpad1')) chooseOption(0);
    if (keys.has('2') || keys.has('numpad2')) chooseOption(1);
    if (keys.has('3') || keys.has('numpad3')) chooseOption(2);
    return;
  }
  if (state.scene === 'fin') return;
  if (keys.has('q')) { keys.delete('q'); state.scene = 'menu'; return; }

  state.time += dt;
  const pos = pointAt(state.t);
  const velocidad = state.speed + (state.quimio / 4000) + (state.capacitacion / 8000);
  state.t += velocidad * dt;
  state.energy = Math.max(0, state.energy - dt * 2);
  if (state.energy <= 0 || state.integridad <= 0) { state.resultado = 'fracaso'; state.scene = 'fin'; }

  const secBreaks = [0.0, 0.18, 0.36, 0.58, 0.78, 0.95];
  for (let i = 0; i < secBreaks.length - 1; i++) if (state.t >= secBreaks[i] && state.t < secBreaks[i + 1]) state.section = i;

  for (const ev of eventos) {
    if (!state.resuelto[ev.id] && state.section === ev.section && Math.abs(state.t - ev.t) < 0.01) {
      state.scene = 'evento';
      state.actualEvento = ev;
    }
  }

  if (state.t >= 0.98) {
    if (state.capacitacion >= 80 && state.quimio >= 40) { state.resultado = 'exito'; }
    else { state.resultado = 'fracaso'; }
    state.scene = 'fin';
    state.t = 0.98;
  }
}

function chooseOption(i) {
  const ev = state.actualEvento;
  if (!ev) return;
  const op = ev.opciones[i];
  if (!op) return;
  op.ef(state);
  state.resuelto[ev.id] = true;
  state.scene = 'juego';
}

function drawBackground() {
  const W = canvas.width, H = canvas.height;
  ctx.fillStyle = '#0d0f14';
  ctx.fillRect(0, 0, W, H);
  const g = ctx.createLinearGradient(0, H * 0.65, 0, H);
  g.addColorStop(0, '#350b0f');
  g.addColorStop(1, '#7a141b');
  ctx.fillStyle = g;
  ctx.fillRect(0, H * 0.55, W, H * 0.45);
  ctx.strokeStyle = 'rgba(255,255,255,0.05)';
  for (let y = H * 0.55; y < H; y += 18) {
    ctx.beginPath();
    for (let x = 0; x < W; x += 14) {
      const yy = y + Math.sin(x * 0.02 + y * 0.01) * 6;
      if (x === 0) ctx.moveTo(x, yy); else ctx.lineTo(x, yy);
    }
    ctx.stroke();
  }
}

function drawPath() {
  ctx.strokeStyle = 'rgba(255,255,255,0.2)';
  ctx.lineWidth = 3;
  ctx.beginPath();
  for (let i = 0; i < path.length; i++) {
    const p = path[i];
    if (i === 0) ctx.moveTo(p.x, p.y); else ctx.lineTo(p.x, p.y);
  }
  ctx.stroke();
}

function drawSperm(x, y, ang) {
  ctx.save();
  ctx.translate(x, y);
  ctx.rotate(ang);
  const headG = ctx.createRadialGradient(0, 0, 2, 0, 0, 16);
  headG.addColorStop(0, '#f0f5ff');
  headG.addColorStop(1, '#bfc7d9');
  ctx.fillStyle = headG;
  ctx.beginPath();
  ctx.ellipse(0, 0, 16, 10, 0, 0, Math.PI * 2);
  ctx.fill();
  ctx.strokeStyle = 'rgba(255,255,255,0.4)';
  ctx.lineWidth = 2;
  ctx.beginPath();
  for (let i = 0; i < 60; i++) {
    const t = i / 59;
    const wx = 10 + t * 60;
    const wy = Math.sin((state.time * 6 + t * 10)) * (6 - t * 4);
    if (i === 0) ctx.moveTo(wx, wy); else ctx.lineTo(wx, wy);
  }
  ctx.stroke();
  ctx.restore();
}

function drawOocyte() {
  const p = path[path.length - 1];
  const x = p.x, y = p.y;
  const z = ctx.createRadialGradient(x, y, 8, x, y, 46);
  z.addColorStop(0, '#fff5d6');
  z.addColorStop(1, '#e0c27a');
  ctx.fillStyle = z;
  ctx.beginPath();
  ctx.arc(x, y, 40, 0, Math.PI * 2);
  ctx.fill();
  ctx.strokeStyle = 'rgba(255,255,255,0.3)';
  ctx.lineWidth = 4;
  ctx.beginPath();
  ctx.arc(x, y, 48, 0, Math.PI * 2);
  ctx.stroke();
}

function drawHUD() {
  const W = canvas.width;
  const bars = [
    { label: 'Energía', val: state.energy, color: '#4caf50' },
    { label: 'Integridad', val: state.integridad, color: '#f44336' },
    { label: 'Capacitación', val: state.capacitacion, color: '#ff9800' },
    { label: 'Quimiotaxis', val: state.quimio, color: '#03a9f4' }
  ];
  ctx.font = '16px system-ui, -apple-system, Segoe UI';
  ctx.textBaseline = 'top';
  for (let i = 0; i < bars.length; i++) {
    const b = bars[i];
    const x = 20, y = 20 + i * 26;
    ctx.fillStyle = '#333';
    ctx.fillRect(x, y, 240, 16);
    ctx.fillStyle = b.color;
    ctx.fillRect(x, y, 240 * Math.max(0, Math.min(1, b.val / 100)), 16);
    ctx.fillStyle = '#fff';
    ctx.fillText(b.label, x, y - 18);
  }
  ctx.textAlign = 'right';
  ctx.fillStyle = '#fff';
  ctx.fillText('Tiempo ' + Math.floor(state.time) + 's', W - 20, 20);
}

function drawEvento(ev) {
  const W = canvas.width, H = canvas.height;
  ctx.fillStyle = 'rgba(0,0,0,0.5)';
  ctx.fillRect(0, 0, W, H);
  ctx.fillStyle = '#fff';
  ctx.font = 'bold 28px system-ui';
  ctx.textAlign = 'center';
  ctx.textBaseline = 'top';
  ctx.fillText(ev.titulo, W / 2, H * 0.20);
  ctx.font = '20px system-ui';
  for (let i = 0; i < ev.opciones.length; i++) {
    const txt = ev.opciones[i].txt;
    const bx = W * 0.5 - 240;
    const by = H * (0.35 + i * 0.12);
    ctx.fillStyle = 'rgba(255,255,255,0.08)';
    ctx.fillRect(bx, by, 480, 48);
    ctx.fillStyle = '#fff';
    ctx.textAlign = 'left';
    ctx.fillText((i + 1) + '. ' + txt, bx + 16, by + 12);
  }
  ctx.textAlign = 'center';
  ctx.fillStyle = '#ccc';
  ctx.fillText('Pulsa 1/2/3 para decidir', W / 2, H * 0.75);
}

function render() {
  ctx.clearRect(0, 0, canvas.width, canvas.height);
  drawBackground();
  drawPath();
  drawOocyte();
  if (state.scene === 'menu') {
    ctx.fillStyle = '#fff';
    ctx.font = 'bold 42px system-ui';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillText('Camino a la Fecundación', canvas.width / 2, canvas.height * 0.32);
    ctx.font = '20px system-ui';
    ctx.fillStyle = '#ddd';
    ctx.fillText('Objetivo: alcanzar y fusionarse con el ovocito', canvas.width / 2, canvas.height * 0.42);
    ctx.fillText('Espacio/Enter para comenzar · Q: Volver al menú', canvas.width / 2, canvas.height * 0.52);
    return;
  }
  const pos = pointAt(state.t);
  drawSperm(pos.x, pos.y, pos.angle);
  drawHUD();
  if (state.scene === 'evento') drawEvento(state.actualEvento);
  if (state.scene === 'fin') {
    ctx.fillStyle = 'rgba(0,0,0,0.6)';
    ctx.fillRect(0, 0, canvas.width, canvas.height);
    ctx.fillStyle = '#fff';
    ctx.font = 'bold 40px system-ui';
    ctx.textAlign = 'center';
    const msg = state.resultado === 'exito' ? '¡Fecundación lograda!' : 'No se produjo la fecundación';
    ctx.fillText(msg, canvas.width / 2, canvas.height * 0.4);
    ctx.font = '20px system-ui';
    ctx.fillStyle = '#ddd';
    ctx.fillText('Espacio/Enter para reiniciar · Q: Menú', canvas.width / 2, canvas.height * 0.52);
    if (keys.has(' ') || keys.has('enter')) { keys.clear(); startGame(); }
    if (keys.has('q')) { keys.delete('q'); state.scene = 'menu'; }
  }
}

function frame(now) {
  const dt = 1 / 60;
  state.time += dt;
  update(dt);
  render();
  requestAnimationFrame(frame);
}

initPath();
state.scene = 'menu';
requestAnimationFrame(frame);