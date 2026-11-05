const API_BASE = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/clinica-citas/api').replace(/\/$/, '');

export async function request(path, options = {}) {
  let response;
  try {
    response = await fetch(`${API_BASE}${path}`, {
      ...options,
      headers: { Accept: 'application/json', ...(options.body ? { 'Content-Type': 'application/json' } : {}), ...options.headers },
    });
  } catch {
    throw new Error(`No se pudo conectar con la API (${API_BASE}). Confirma que el backend esté iniciado y permita solicitudes desde http://localhost:5173.`);
  }

  const text = await response.text();
  let data = null;
  if (text) {
    try { data = JSON.parse(text); } catch { data = text; }
  }
  if (!response.ok) {
    const message = data?.mensaje || data?.message || `La solicitud falló (HTTP ${response.status}).`;
    throw new Error(message);
  }
  return data;
}

// Rutas previstas. Confirmar sus nombres y verbos con la implementación REST.
export const api = {
  citas: (pacienteId) => request(`/citas${pacienteId ? `?idPaciente=${encodeURIComponent(pacienteId)}` : ''}`),
  crearCita: (payload) => request('/citas', { method: 'POST', body: JSON.stringify(payload) }),
  actualizarCita: (id, payload) => request(`/citas/${id}`, { method: 'PUT', body: JSON.stringify(payload) }),
  cancelarCita: (id) => request(`/citas/${id}/cancelar`, { method: 'PATCH' }),
  pacientes: () => request('/pacientes'),
  medicos: () => request('/medicos'),
};

export function fechaParaApi(value) {
  if (!value) return '';
  const [year, month, day] = value.split('-');
  return `${day}/${month}/${year}`;
}
