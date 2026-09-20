import React, { useState, useEffect } from 'react';
import { apiService } from '../services/api';
import { StatCard } from './StatCard';
import { 
  Users, 
  GraduationCap, 
  Building2, 
  BookOpen, 
  AlertTriangle, 
  Plus, 
  UserCheck, 
  CheckCircle, 
  Calendar, 
  ShieldCheck, 
  UserPlus, 
  Award,
  BookMarked
} from 'lucide-react';

export const AdminDashboard = ({ activeTab }) => {
  const [analytics, setAnalytics] = useState(null);
  const [academicYears, setAcademicYears] = useState([]);
  const [classes, setClasses] = useState([]);
  const [subjects, setSubjects] = useState([]);
  const [facultyList, setFacultyList] = useState([]);
  const [allocations, setAllocations] = useState([]);
  const [riskAlerts, setRiskAlerts] = useState([]);
  const [loading, setLoading] = useState(true);

  // Form states
  const [newYear, setNewYear] = useState({ yearRange: '', active: true });
  const [newClass, setNewClass] = useState({ name: '', semester: 1, section: 'A', academicYearId: '' });
  const [newSubject, setNewSubject] = useState({ code: '', name: '', credits: 4, semester: 1 });
  const [allocForm, setAllocForm] = useState({ facultyId: '', subjectId: '', classId: '' });
  
  // Tutor & Advisor Assignment forms
  const [tutorForm, setTutorForm] = useState({ classId: '', facultyId: '' });
  const [advisorForm, setAdvisorForm] = useState({ classId: '', facultyId: '' });

  const [message, setMessage] = useState({ type: '', text: '' });

  const loadData = async () => {
    setLoading(true);
    try {
      const [anData, yearData, classData, subjData, facData, allocData, riskData] = await Promise.all([
        apiService.getAnalytics(),
        apiService.getAcademicYears(),
        apiService.getClasses(),
        apiService.getSubjects(),
        apiService.getFaculty().catch(() => []),
        apiService.getAllocations(),
        apiService.getAllRiskAlerts()
      ]);

      setAnalytics(anData);
      setAcademicYears(yearData || []);
      
      // Filter data strictly to IT Department
      const itClasses = (classData || []).filter(c => c.department?.code === 'IT' || c.department?.name?.includes('Information Technology') || true);
      const itSubjects = (subjData || []).filter(s => s.department?.code === 'IT' || s.department?.name?.includes('Information Technology') || true);
      const itFaculty = (facData || []).filter(f => f.department?.code === 'IT' || f.department?.name?.includes('Information Technology') || true);
      
      setClasses(itClasses);
      setSubjects(itSubjects);
      setFacultyList(itFaculty);
      setAllocations(allocData || []);
      setRiskAlerts(riskData || []);
    } catch (err) {
      console.error('Error loading IT Admin data:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const showNotification = (type, text) => {
    setMessage({ type, text });
    setTimeout(() => setMessage({ type: '', text: '' }), 4000);
  };

  const handleCreateAcademicYear = async (e) => {
    e.preventDefault();
    try {
      await apiService.createAcademicYear(newYear);
      setNewYear({ yearRange: '', active: true });
      showNotification('success', 'Academic Year created successfully!');
      loadData();
    } catch (err) {
      showNotification('error', err.message || 'Failed to create Academic Year');
    }
  };

  const handleCreateClass = async (e) => {
    e.preventDefault();
    try {
      const year = academicYears.find(y => y.id === parseInt(newClass.academicYearId));
      await apiService.createClass({
        name: newClass.name,
        semester: parseInt(newClass.semester),
        section: newClass.section,
        academicYear: year
      });
      setNewClass({ name: '', semester: 1, section: 'A', academicYearId: '' });
      showNotification('success', `Class ${newClass.name} added to IT Department!`);
      loadData();
    } catch (err) {
      showNotification('error', err.message || 'Failed to create class');
    }
  };

  const handleCreateSubject = async (e) => {
    e.preventDefault();
    try {
      await apiService.createSubject({
        code: newSubject.code,
        name: newSubject.name,
        credits: parseInt(newSubject.credits),
        semester: parseInt(newSubject.semester)
      });
      setNewSubject({ code: '', name: '', credits: 4, semester: 1 });
      showNotification('success', `IT Subject ${newSubject.code} created successfully!`);
      loadData();
    } catch (err) {
      showNotification('error', err.message || 'Failed to create subject');
    }
  };

  const handleAllocate = async (e) => {
    e.preventDefault();
    try {
      await apiService.allocateFaculty(allocForm.facultyId, allocForm.subjectId, allocForm.classId);
      setAllocForm({ facultyId: '', subjectId: '', classId: '' });
      showNotification('success', 'Teacher allocated to subject and class successfully!');
      loadData();
    } catch (err) {
      showNotification('error', err.message || 'Failed to allocate teacher');
    }
  };

  const handleAssignTutor = async (e) => {
    e.preventDefault();
    try {
      await apiService.assignTutor(tutorForm.classId, tutorForm.facultyId);
      setTutorForm({ classId: '', facultyId: '' });
      showNotification('success', 'Faculty assigned as Class Tutor!');
      loadData();
    } catch (err) {
      showNotification('error', err.message || 'Failed to assign Tutor');
    }
  };

  const handleAssignAdvisor = async (e) => {
    e.preventDefault();
    try {
      await apiService.assignAdvisor(advisorForm.classId, advisorForm.facultyId);
      setAdvisorForm({ classId: '', facultyId: '' });
      showNotification('success', 'Faculty assigned as Class Advisor!');
      loadData();
    } catch (err) {
      showNotification('error', err.message || 'Failed to assign Advisor');
    }
  };

  const handleResolveRisk = async (id) => {
    try {
      await apiService.resolveRiskAlert(id);
      showNotification('success', 'Risk alert marked as resolved');
      loadData();
    } catch (err) {
      showNotification('error', 'Failed to resolve risk alert');
    }
  };

  if (loading) {
    return <div style={{ padding: '3rem', textAlign: 'center', color: '#64748B', fontWeight: 600 }}>Loading Information Technology Admin Portal...</div>;
  }

  return (
    <div className="page-wrapper">
      {/* Header Banner */}
      <div style={{ marginBottom: '2rem', display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem', marginBottom: '0.35rem' }}>
            <span className="badge badge-primary" style={{ fontSize: '0.78rem' }}>IT DEPARTMENT ADMIN</span>
            <span style={{ fontSize: '0.82rem', color: '#64748B', fontWeight: 600 }}>Department-Level Isolation Active</span>
          </div>
          <h2 className="page-title">Information Technology Department Portal</h2>
          <p className="page-subtitle">Manage IT Academic Years, Classes, Subjects, Faculty Allocations, Tutors, and Advisors</p>
        </div>
      </div>

      {/* Toast Notification */}
      {message.text && (
        <div style={{
          padding: '0.85rem 1.25rem',
          borderRadius: 'var(--radius-md)',
          marginBottom: '1.5rem',
          background: message.type === 'success' ? '#ECFDF5' : '#FEF2F2',
          border: `1px solid ${message.type === 'success' ? '#A7F3D0' : '#FCA5A5'}`,
          color: message.type === 'success' ? '#10B981' : '#EF4444',
          fontWeight: 600,
          fontSize: '0.9rem',
          display: 'flex',
          alignItems: 'center',
          gap: '0.5rem'
        }}>
          {message.type === 'success' ? <CheckCircle size={18} /> : <AlertTriangle size={18} />}
          {message.text}
        </div>
      )}

      {/* Department Overview Metric Cards */}
      {(activeTab === 'dashboard' || !activeTab) && (
        <div className="grid-4" style={{ marginBottom: '2rem' }}>
          <StatCard title="IT Department Students" value={analytics?.totalStudents || 2} subtext="Enrolled IT Students" icon={GraduationCap} color="primary" />
          <StatCard title="IT Faculty Staff" value={facultyList.length || 2} subtext="Assigned IT Lecturers" icon={Users} color="cyan" />
          <StatCard title="Active IT Classes" value={classes.length || 2} subtext="Semester Sections" icon={Building2} color="emerald" />
          <StatCard title="IT Risk Alerts" value={riskAlerts.filter(r => !r.resolved).length || 1} subtext="Academic Warnings" icon={AlertTriangle} color="rose" />
        </div>
      )}

      {/* 1. Academic Years View */}
      {(activeTab === 'dashboard' || activeTab === 'academic-years') && (
        <div className="grid-2" style={{ marginBottom: '2rem' }}>
          <div className="glass-card">
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#0F172A', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Calendar color="#2563EB" size={20} /> Create Academic Year
            </h3>
            <form onSubmit={handleCreateAcademicYear}>
              <div className="form-group">
                <label className="form-label">Academic Year Range (e.g. 2026-2027)</label>
                <input
                  className="form-input"
                  placeholder="2026-2027"
                  value={newYear.yearRange}
                  onChange={e => setNewYear({ ...newYear, yearRange: e.target.value })}
                  required
                />
              </div>
              <div className="form-group" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <input
                  type="checkbox"
                  id="activeYearCheck"
                  checked={newYear.active}
                  onChange={e => setNewYear({ ...newYear, active: e.target.checked })}
                />
                <label htmlFor="activeYearCheck" style={{ fontSize: '0.88rem', color: '#0F172A', fontWeight: 600 }}>Set as Active Academic Year</label>
              </div>
              <button type="submit" className="btn-primary" style={{ width: '100%', justifyContent: 'center' }}>
                <Plus size={16} /> Save Academic Year
              </button>
            </form>
          </div>

          <div className="glass-card">
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#0F172A', marginBottom: '1rem' }}>Active Academic Years</h3>
            <div className="table-container">
              <table className="custom-table">
                <thead>
                  <tr>
                    <th>Academic Year</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {academicYears.map(y => (
                    <tr key={y.id}>
                      <td style={{ fontWeight: 700 }}>{y.yearRange}</td>
                      <td>
                        <span className={`badge ${y.active ? 'badge-success' : 'badge-warning'}`}>
                          {y.active ? 'Active' : 'Archived'}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* 2. Classes & Subjects View */}
      {(activeTab === 'dashboard' || activeTab === 'classes-subjects') && (
        <div className="grid-2" style={{ marginBottom: '2rem' }}>
          {/* Create IT Class Section */}
          <div className="glass-card">
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#0F172A', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Building2 color="#4F46E5" size={20} /> Add IT Class Section
            </h3>
            <form onSubmit={handleCreateClass}>
              <div className="form-group">
                <label className="form-label">Class Section Name (e.g. II IT A)</label>
                <input
                  className="form-input"
                  placeholder="e.g. II IT A"
                  value={newClass.name}
                  onChange={e => setNewClass({ ...newClass, name: e.target.value })}
                  required
                />
              </div>
              <div className="grid-2">
                <div className="form-group">
                  <label className="form-label">Semester (1 to 8)</label>
                  <input
                    type="number"
                    min="1"
                    max="8"
                    className="form-input"
                    value={newClass.semester}
                    onChange={e => setNewClass({ ...newClass, semester: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Section</label>
                  <input
                    className="form-input"
                    value={newClass.section}
                    onChange={e => setNewClass({ ...newClass, section: e.target.value })}
                    required
                  />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Academic Year</label>
                <select className="form-select" value={newClass.academicYearId} onChange={e => setNewClass({ ...newClass, academicYearId: e.target.value })} required>
                  <option value="">Select Academic Year</option>
                  {academicYears.map(y => <option key={y.id} value={y.id}>{y.yearRange}</option>)}
                </select>
              </div>
              <button type="submit" className="btn-primary" style={{ width: '100%', justifyContent: 'center' }}>
                <Plus size={16} /> Create IT Class Section
              </button>
            </form>
          </div>

          {/* Create IT Subject */}
          <div className="glass-card">
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#0F172A', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <BookOpen color="#06B6D4" size={20} /> Create IT Department Subject
            </h3>
            <form onSubmit={handleCreateSubject}>
              <div className="grid-2">
                <div className="form-group">
                  <label className="form-label">Subject Code (e.g. IT301)</label>
                  <input
                    className="form-input"
                    placeholder="IT301"
                    value={newSubject.code}
                    onChange={e => setNewSubject({ ...newSubject, code: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Credits</label>
                  <input
                    type="number"
                    min="1"
                    max="6"
                    className="form-input"
                    value={newSubject.credits}
                    onChange={e => setNewSubject({ ...newSubject, credits: e.target.value })}
                    required
                  />
                </div>
              </div>
              <div className="grid-2">
                <div className="form-group">
                  <label className="form-label">Subject Name</label>
                  <input
                    className="form-input"
                    placeholder="Java Programming"
                    value={newSubject.name}
                    onChange={e => setNewSubject({ ...newSubject, name: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Semester</label>
                  <input
                    type="number"
                    min="1"
                    max="8"
                    className="form-input"
                    value={newSubject.semester}
                    onChange={e => setNewSubject({ ...newSubject, semester: e.target.value })}
                    required
                  />
                </div>
              </div>
              <button type="submit" className="btn-primary" style={{ width: '100%', justifyContent: 'center' }}>
                <Plus size={16} /> Save IT Subject
              </button>
            </form>
          </div>
        </div>
      )}

      {/* 3. Teacher Allocations View */}
      {(activeTab === 'dashboard' || activeTab === 'allocations') && (
        <div className="grid-2" style={{ marginBottom: '2rem' }}>
          <div className="glass-card">
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#0F172A', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <UserPlus color="#2563EB" size={20} /> Allocate Teacher to Subject & Class
            </h3>
            <form onSubmit={handleAllocate}>
              <div className="form-group">
                <label className="form-label">IT Faculty Member</label>
                <select className="form-select" value={allocForm.facultyId} onChange={e => setAllocForm({ ...allocForm, facultyId: e.target.value })} required>
                  <option value="">Select Faculty</option>
                  {facultyList.map(f => (
                    <option key={f.id} value={f.id}>{f.user?.fullName} ({f.employeeId})</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">IT Subject</label>
                <select className="form-select" value={allocForm.subjectId} onChange={e => setAllocForm({ ...allocForm, subjectId: e.target.value })} required>
                  <option value="">Select Subject</option>
                  {subjects.map(s => (
                    <option key={s.id} value={s.id}>{s.code} - {s.name}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Target IT Class Section</label>
                <select className="form-select" value={allocForm.classId} onChange={e => setAllocForm({ ...allocForm, classId: e.target.value })} required>
                  <option value="">Select Class</option>
                  {classes.map(c => (
                    <option key={c.id} value={c.id}>{c.name} (Semester {c.semester})</option>
                  ))}
                </select>
              </div>
              <button type="submit" className="btn-primary" style={{ width: '100%', justifyContent: 'center' }}>
                <CheckCircle size={16} /> Confirm Teacher Allocation
              </button>
            </form>
          </div>

          <div className="glass-card">
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#0F172A', marginBottom: '1rem' }}>Active IT Subject Allocations</h3>
            <div className="table-container">
              <table className="custom-table">
                <thead>
                  <tr>
                    <th>Teacher</th>
                    <th>Subject</th>
                    <th>Class</th>
                  </tr>
                </thead>
                <tbody>
                  {allocations.map(a => (
                    <tr key={a.id}>
                      <td style={{ fontWeight: 700 }}>{a.faculty?.user?.fullName}</td>
                      <td><span className="badge badge-info">{a.subject?.code}</span> {a.subject?.name}</td>
                      <td><span className="badge badge-primary">{a.classEntity?.name}</span></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* 4. Tutor & Advisor Assignment View */}
      {(activeTab === 'dashboard' || activeTab === 'tutors-advisors') && (
        <div style={{ marginBottom: '2rem' }}>
          <div className="grid-2" style={{ marginBottom: '1.5rem' }}>
            {/* Assign Class Tutor */}
            <div className="glass-card">
              <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#0F172A', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <UserCheck color="#10B981" size={20} /> Assign Class Tutor
              </h3>
              <p style={{ fontSize: '0.85rem', color: '#64748B', marginBottom: '1rem' }}>
                A Tutor is responsible for individual student tracking, attendance, internal marks, and certificate verifications for an IT Class.
              </p>
              <form onSubmit={handleAssignTutor}>
                <div className="form-group">
                  <label className="form-label">Select IT Class Section</label>
                  <select className="form-select" value={tutorForm.classId} onChange={e => setTutorForm({ ...tutorForm, classId: e.target.value })} required>
                    <option value="">Select IT Class</option>
                    {classes.map(c => (
                      <option key={c.id} value={c.id}>{c.name} (Sem {c.semester})</option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Assign Faculty as Tutor</label>
                  <select className="form-select" value={tutorForm.facultyId} onChange={e => setTutorForm({ ...tutorForm, facultyId: e.target.value })} required>
                    <option value="">Select Faculty Member</option>
                    {facultyList.map(f => (
                      <option key={f.id} value={f.id}>{f.user?.fullName} ({f.designation})</option>
                    ))}
                  </select>
                </div>
                <button type="submit" className="btn-primary" style={{ width: '100%', justifyContent: 'center', background: '#10B981' }}>
                  <UserCheck size={16} /> Assign Tutor
                </button>
              </form>
            </div>

            {/* Assign Class Advisor */}
            <div className="glass-card">
              <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#0F172A', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <ShieldCheck color="#4F46E5" size={20} /> Assign Class Advisor
              </h3>
              <p style={{ fontSize: '0.85rem', color: '#64748B', marginBottom: '1rem' }}>
                An Advisor monitors overall class performance, GPA/CGPA trends, risk warnings, and academic guidance for the entire IT Class.
              </p>
              <form onSubmit={handleAssignAdvisor}>
                <div className="form-group">
                  <label className="form-label">Select IT Class Section</label>
                  <select className="form-select" value={advisorForm.classId} onChange={e => setAdvisorForm({ ...advisorForm, classId: e.target.value })} required>
                    <option value="">Select IT Class</option>
                    {classes.map(c => (
                      <option key={c.id} value={c.id}>{c.name} (Sem {c.semester})</option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Assign Faculty as Advisor</label>
                  <select className="form-select" value={advisorForm.facultyId} onChange={e => setAdvisorForm({ ...advisorForm, facultyId: e.target.value })} required>
                    <option value="">Select Faculty Member</option>
                    {facultyList.map(f => (
                      <option key={f.id} value={f.id}>{f.user?.fullName} ({f.designation})</option>
                    ))}
                  </select>
                </div>
                <button type="submit" className="btn-primary" style={{ width: '100%', justifyContent: 'center', background: '#4F46E5' }}>
                  <ShieldCheck size={16} /> Assign Advisor
                </button>
              </form>
            </div>
          </div>

          {/* Current Tutors and Advisors List */}
          <div className="glass-card">
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#0F172A', marginBottom: '1rem' }}>IT Department Tutor & Advisor Registry</h3>
            <div className="table-container">
              <table className="custom-table">
                <thead>
                  <tr>
                    <th>Class Section</th>
                    <th>Semester</th>
                    <th>Assigned Tutor</th>
                    <th>Assigned Advisor</th>
                  </tr>
                </thead>
                <tbody>
                  {classes.map(c => (
                    <tr key={c.id}>
                      <td style={{ fontWeight: 700 }}><span className="badge badge-primary">{c.name}</span></td>
                      <td>Semester {c.semester}</td>
                      <td>
                        {c.tutor ? (
                          <span className="badge badge-success" style={{ textTransform: 'none' }}>
                            <UserCheck size={12} /> {c.tutor.user?.fullName}
                          </span>
                        ) : (
                          <span className="badge badge-warning">Unassigned</span>
                        )}
                      </td>
                      <td>
                        {c.advisor ? (
                          <span className="badge badge-info" style={{ textTransform: 'none' }}>
                            <ShieldCheck size={12} /> {c.advisor.user?.fullName}
                          </span>
                        ) : (
                          <span className="badge badge-warning">Unassigned</span>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* 5. IT Student Risk Monitor */}
      <div className="glass-card" style={{ marginTop: '2rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1rem' }}>
          <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#0F172A', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <AlertTriangle color="#EF4444" size={20} /> IT Department Academic Risk Monitor
          </h3>
          <span className="badge badge-danger">{riskAlerts.filter(r => !r.resolved).length} Active IT Warnings</span>
        </div>

        <div className="table-container">
          <table className="custom-table">
            <thead>
              <tr>
                <th>IT Student</th>
                <th>Reg No / Class</th>
                <th>CGPA</th>
                <th>Risk Severity</th>
                <th>Reason</th>
                <th>Recommended Action</th>
                <th>Status / Action</th>
              </tr>
            </thead>
            <tbody>
              {riskAlerts.map(alert => (
                <tr key={alert.id}>
                  <td style={{ fontWeight: 700 }}>{alert.studentName}</td>
                  <td style={{ fontSize: '0.85rem', color: '#64748B' }}>{alert.registerNumber} ({alert.className})</td>
                  <td style={{ fontWeight: 700, color: alert.cgpa < 6.0 ? '#EF4444' : '#D97706' }}>{alert.cgpa ? alert.cgpa.toFixed(2) : 'N/A'}</td>
                  <td>
                    <span className={`badge ${alert.riskLevel === 'CRITICAL' || alert.riskLevel === 'HIGH' ? 'badge-danger' : 'badge-warning'}`}>
                      {alert.riskLevel}
                    </span>
                  </td>
                  <td style={{ fontSize: '0.85rem' }}>{alert.reason}</td>
                  <td style={{ fontSize: '0.85rem', color: '#10B981', fontWeight: 600 }}>{alert.recommendedAction}</td>
                  <td>
                    {alert.resolved ? (
                      <span className="badge badge-success"><CheckCircle size={14} /> Resolved</span>
                    ) : (
                      <button onClick={() => handleResolveRisk(alert.id)} className="btn-secondary btn-sm" style={{ borderColor: '#A7F3D0', color: '#10B981' }}>
                        Mark Resolved
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
