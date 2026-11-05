import { useCallback, useEffect, useState } from 'react';
import { api, fechaParaApi } from './api.js';

const blankForm = { idPaciente: '', idMedico: '', fecha: '', hora: '', motivo: '' };

function ErrorBanner({ children, onClose }) {
  if (!children) return null;
  return <div className="notice error" role="alert"><span>{children}</span><button className="icon-button" onClick={onClose} aria-label="Cerrar">×</button></div>;
}

function App() {
  const [patientId, setPatientId] = useState('');
  const [appointments, setAppointments] = useState([]);
  const [patients, setPatients] = useState([]);
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [form, setForm] = useState(blankForm);
  const [editingId, setEditingId] = useState(null);

  const loadLists = useCallback(async () => {
    setLoading(true); setError('');
    try {
      const [citas, pacientes, medicos] = await Promise.all([
        api.citas(patientId.trim()), api.pacientes(), api.medicos(),
      ]);
      setAppointments(Array.isArray(citas) ? citas : (citas?.datos || citas?.citas || []));
      setPatients(Array.isArray(pacientes) ? pacientes : (pacientes?.datos || pacientes?.pacientes || []));
      setDoctors(Array.isArray(medicos) ? medicos : (medicos?.datos || medicos?.medicos || []));
    } catch (e) { setError(e.message); }
    finally { setLoading(false); }
  }, [patientId]);

  useEffect(() => { loadLists(); }, [loadLists]);

  function change(name, value) { setForm((current) => ({ ...current, [name]: value })); }
  function startEdit(cita) {
    setEditingId(cita.idCita);
    const [day, month, year] = (cita.fecha || '').split('/');
    setForm({ idPaciente: cita.idPaciente ?? '', idMedico: cita.idMedico ?? '', fecha: year ? `${year}-${month}-${day}` : '', hora: cita.hora || '', motivo: cita.motivo || '' });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
  function resetForm() { setForm(blankForm); setEditingId(null); }

  async function submit(event) {
    event.preventDefault(); setSaving(true); setError(''); setSuccess('');
    const payload = { idPaciente: Number(form.idPaciente), idMedico: Number(form.idMedico), fecha: fechaParaApi(form.fecha), hora: form.hora, motivo: form.motivo.trim() };
    try {
      if (editingId) await api.actualizarCita(editingId, payload);
      else await api.crearCita(payload);
      setSuccess(editingId ? 'La cita se actualizó correctamente.' : 'La cita se agendó correctamente.');
      resetForm(); await loadLists();
    } catch (e) { setError(e.message); }
    finally { setSaving(false); }
  }

  async function cancel(cita) {
    if (!window.confirm(`¿Deseas cancelar la cita #${cita.idCita}?`)) return;
    setError(''); setSuccess('');
    try { await api.cancelarCita(cita.idCita); setSuccess(`La cita #${cita.idCita} fue cancelada.`); await loadLists(); }
    catch (e) { setError(e.message); }
  }

  const activeCount = appointments.filter((c) => ['PROGRAMADA', 'REPROGRAMADA'].includes(c.estado)).length;

  return <div className="shell">
    <header className="topbar"><a className="brand" href="#inicio"><span className="brand-mark">+</span><span>Clínica<span className="brand-muted"> / Citas</span></span></a><span className="top-status"><i /> Portal de citas</span></header>
    <main id="inicio" className="main-content">
      <section className="intro"><div><p className="eyebrow">GESTIÓN DE CITAS</p><h1>Atención médica,<br /><span>más sencilla.</span></h1><p className="intro-copy">Consulta y administra tus citas en un solo lugar.</p></div><div className="intro-art" aria-hidden="true"><div className="art-circle"><span>✚</span></div><div className="art-card"><b>Tu salud</b><small>merece atención</small></div></div></section>
      <ErrorBanner onClose={() => setError('')}>{error}</ErrorBanner>
      {success && <div className="notice success" role="status"><span>{success}</span><button className="icon-button" onClick={() => setSuccess('')} aria-label="Cerrar">×</button></div>}

      <section className="summary-row"><div className="section-heading"><div><p className="eyebrow">TU ESPACIO</p><h2>Administrar citas</h2></div><div className="count-card"><span className="count-icon">▦</span><div><strong>{loading ? '—' : activeCount}</strong><small>citas activas</small></div></div></div></section>

      <section className="content-grid">
        <article className="panel form-panel"><div className="panel-heading"><div className="heading-icon">{editingId ? '✎' : '+'}</div><div><h3>{editingId ? `Editar cita #${editingId}` : 'Agendar una cita'}</h3><p>Completa los datos para reservar tu atención.</p></div></div>
          <form onSubmit={submit}>
            <label>Paciente<select required value={form.idPaciente} onChange={(e) => change('idPaciente', e.target.value)}><option value="">Selecciona un paciente</option>{patients.map((p) => <option key={p.idPaciente} value={p.idPaciente}>{p.nombreCompleto || [p.nombres, p.apellidos].filter(Boolean).join(' ') || `Paciente #${p.idPaciente}`}</option>)}</select></label>
            <label>Médico<select required value={form.idMedico} onChange={(e) => change('idMedico', e.target.value)}><option value="">Selecciona un médico</option>{doctors.map((m) => <option key={m.idMedico} value={m.idMedico}>{m.nombreCompleto || [m.nombres, m.apellidos].filter(Boolean).join(' ') || `Médico #${m.idMedico}`}{m.especialidad ? ` · ${typeof m.especialidad === 'string' ? m.especialidad : m.especialidad.nombre}` : ''}</option>)}</select></label>
            <div className="field-row"><label>Fecha<input required type="date" value={form.fecha} onChange={(e) => change('fecha', e.target.value)} /></label><label>Hora<input required type="time" value={form.hora} onChange={(e) => change('hora', e.target.value)} /></label></div>
            <label>Motivo de consulta <span className="optional">(opcional)</span><textarea maxLength="255" rows="3" value={form.motivo} onChange={(e) => change('motivo', e.target.value)} placeholder="Cuéntanos brevemente el motivo de tu cita" /></label>
            <div className="form-actions"><button className="primary-button" type="submit" disabled={saving || loading}>{saving ? 'Guardando…' : editingId ? 'Guardar cambios' : 'Confirmar cita'} <span>→</span></button>{editingId && <button className="text-button" type="button" onClick={resetForm}>Dejar de editar</button>}</div>
            <p className="form-footnote">La disponibilidad y las reglas de reserva se validan en el servidor.</p>
          </form>
        </article>

        <article className="panel list-panel"><div className="panel-heading list-heading"><div><p className="eyebrow">AGENDA</p><h3>Tus citas</h3></div><button className="refresh-button" onClick={loadLists} disabled={loading} aria-label="Actualizar citas">↻</button></div>
          <form className="filter-form" onSubmit={(e) => { e.preventDefault(); loadLists(); }}><label htmlFor="patient-filter">Filtrar por número de paciente</label><div className="filter-line"><input id="patient-filter" type="number" min="1" placeholder="Ej. 12" value={patientId} onChange={(e) => setPatientId(e.target.value)} /><button type="submit">Buscar</button></div><small>Déjalo vacío para consultar todas las citas.</small></form>
          <div className="appointment-list" aria-live="polite">{loading ? <div className="empty-state"><span className="spinner" />Cargando citas…</div> : appointments.length === 0 ? <div className="empty-state"><span className="empty-icon">▦</span><b>Aún no hay citas para mostrar</b><span>Cuando haya citas disponibles, aparecerán aquí.</span></div> : appointments.map((cita) => <Appointment key={cita.idCita} cita={cita} onEdit={startEdit} onCancel={cancel} />)}</div>
        </article>
      </section>
      <p className="page-note">Si tienes dificultades para reservar, comunícate con la clínica.</p>
    </main>
    <footer>Clínica · Gestión de citas <span>Atención pensada para ti</span></footer>
  </div>;
}

function Appointment({ cita, onEdit, onCancel }) {
  const state = (cita.estado || 'PROGRAMADA').toUpperCase();
  const active = ['PROGRAMADA', 'REPROGRAMADA'].includes(state);
  return <article className="appointment-card"><div className="appointment-date"><b>{cita.fecha || '—'}</b><span>{cita.hora || '—'}</span></div><div className="appointment-info"><div className="appointment-title"><b>{cita.medico || `Médico #${cita.idMedico ?? '—'}`}</b><span className={`badge ${state.toLowerCase()}`}>{cita.estadoEtiqueta || state}</span></div><span>{cita.especialidad || 'Consulta médica'}</span><small>Paciente: {cita.paciente || `#${cita.idPaciente ?? '—'}`}{cita.motivo ? ` · ${cita.motivo}` : ''}</small><div className="appointment-actions">{active && <><button onClick={() => onEdit(cita)}>Editar</button><button className="cancel-action" onClick={() => onCancel(cita)}>Cancelar cita</button></>}</div></div></article>;
}

export default App;
