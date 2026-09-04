import React, { useState, useEffect } from 'react';
import { apiService } from '../services/api';
import { BookOpen, CheckCircle, XCircle, Save, Award, Users, AlertCircle } from 'lucide-react';

export const FacultyDashboard = ({ activeTab }) => {
  const [assignedAllocations, setAssignedAllocations] = useState([]);
  const [selectedAllocation, setSelectedAllocation] = useState(null);
  const [students, setStudents] = useState([]);
  const [certificates, setCertificates] = useState([]);
  const [loading, setLoading] = useState(true);

  // Marks Entry Form State
  const [markType, setMarkType] = useState('INTERNAL_1');
  const [marksData, setMarksData] = useState({});
  const [maxMarks, setMaxMarks] = useState(50);
  const [semesterMarkData, setSemesterMarkData] = useState({});

  const [verifyComments, setVerifyComments] = useState({});

  const loadFacultyData = async () => {
    setLoading(true);
    try {
      const [allocData, certData] = await Promise.all([
        apiService.getAssignedSubjects(),
        apiService.getAllCertificates()
      ]);
      setAssignedAllocations(allocData);
      setCertificates(certData);
      if (allocData.length > 0) {
        setSelectedAllocation(allocData[0]);
        loadClassStudents(allocData[0].classEntity.id);
      }
    } catch (err) {
      console.error('Error loading faculty data:', err);
    } finally {
      setLoading(false);
    }
  };

  const loadClassStudents = async (classId) => {
    try {
      const studentData = await apiService.getStudentsInClass(classId);
      setStudents(studentData);
    } catch (err) {
      console.error('Error loading class students:', err);
    }
  };

  useEffect(() => {
    loadFacultyData();
  }, []);

  const handleSelectClass = (alloc) => {
    setSelectedAllocation(alloc);
    loadClassStudents(alloc.classEntity.id);
  };

  const handleSaveInternalMark = async (studentId) => {
    if (!selectedAllocation) return;
    const marksObtained = parseFloat(marksData[studentId]);
    if (isNaN(marksObtained)) return;

    await apiService.saveInternalMark({
      studentId,
      subjectId: selectedAllocation.subject.id,
      markType,
      marksObtained,
      maxMarks: parseFloat(maxMarks),
      semester: selectedAllocation.classEntity.semester
    });

    alert('Internal mark saved successfully!');
  };

  const handleSaveSemesterMark = async (studentId) => {
    if (!selectedAllocation) return;
    const score = parseFloat(semesterMarkData[studentId]);
    if (isNaN(score)) return;

    // Calculate Grade Points & Letter Grade automatically
    let gp = 0.0;
    let grade = 'F';
    if (score >= 90) { gp = 10.0; grade = 'O'; }
    else if (score >= 80) { gp = 9.0; grade = 'A+'; }
    else if (score >= 70) { gp = 8.0; grade = 'A'; }
    else if (score >= 60) { gp = 7.0; grade = 'B+'; }
    else if (score >= 50) { gp = 6.0; grade = 'B'; }
    else if (score >= 40) { gp = 5.0; grade = 'C'; }

    await apiService.saveSemesterMark({
      studentId,
      subjectId: selectedAllocation.subject.id,
      semester: selectedAllocation.classEntity.semester,
      marksObtained: score,
      maxMarks: 100.0,
      gradePoints: gp,
      letterGrade: grade
    });

    alert(`Semester mark saved! Grade: ${grade} (${gp} GP)`);
  };

  const handleVerifyCert = async (certId, status) => {
    const comments = verifyComments[certId] || (status === 'APPROVED' ? 'Verified successfully' : 'Documentation insufficient');
    await apiService.verifyCertificate(certId, status, comments);
    loadFacultyData();
  };

  if (loading) {
    return <div style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>Loading Faculty Portal...</div>;
  }

  return (
    <div className="page-wrapper">
      <div style={{ marginBottom: '2rem' }}>
        <h2 className="page-title">Faculty Academic Workspace</h2>
        <p className="page-subtitle">Manage assigned subjects, enter internal/semester marks, and verify student certificates</p>
      </div>

      {activeTab === 'verify-certificates' ? (
        /* Certificate Verification View */
        <div className="glass-card">
          <h3 style={{ fontSize: '1.18rem', fontWeight: 700, marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Award color="#818cf8" size={22} /> Student Certificate Verification Queue
          </h3>
          <div className="table-container">
            <table className="custom-table">
              <thead>
                <tr>
                  <th>Student</th>
                  <th>Reg No</th>
                  <th>Title & Organization</th>
                  <th>Category</th>
                  <th>Verification Status</th>
                  <th>Review Comments</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {certificates.map(cert => (
                  <tr key={cert.id}>
                    <td style={{ fontWeight: 700 }}>{cert.studentName}</td>
                    <td style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>{cert.registerNumber}</td>
                    <td>
                      <div style={{ fontWeight: 600 }}>{cert.title}</div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-dim)' }}>{cert.issueOrganization}</div>
                    </td>
                    <td><span className="badge badge-info">{cert.category}</span></td>
                    <td>
                      <span className={`badge ${cert.status === 'APPROVED' ? 'badge-success' : cert.status === 'REJECTED' ? 'badge-danger' : 'badge-warning'}`}>
                        {cert.status}
                      </span>
                    </td>
                    <td>
                      <input
                        className="form-input"
                        placeholder="Add review notes..."
                        value={verifyComments[cert.id] !== undefined ? verifyComments[cert.id] : (cert.reviewerComments || '')}
                        onChange={e => setVerifyComments({ ...verifyComments, [cert.id]: e.target.value })}
                        style={{ padding: '0.4rem 0.6rem', fontSize: '0.82rem' }}
                      />
                    </td>
                    <td>
                      <div style={{ display: 'flex', gap: '0.5rem' }}>
                        <button onClick={() => handleVerifyCert(cert.id, 'APPROVED')} className="btn-primary btn-sm" style={{ background: '#10B981' }}>
                          <CheckCircle size={14} /> Approve
                        </button>
                        <button onClick={() => handleVerifyCert(cert.id, 'REJECTED')} className="btn-secondary btn-sm" style={{ borderColor: '#FCA5A5', color: '#EF4444' }}>
                          <XCircle size={14} /> Reject
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      ) : (
        /* Marks Entry & Subject Allocations View */
        <div>
          {/* Subject Selector Bar */}
          <div style={{ display: 'flex', gap: '1rem', overflowX: 'auto', paddingBottom: '1rem', marginBottom: '1.5rem' }}>
            {assignedAllocations.map(alloc => {
              const isSelected = selectedAllocation?.id === alloc.id;
              return (
                <button
                  key={alloc.id}
                  onClick={() => handleSelectClass(alloc)}
                  className={`glass-card ${isSelected ? 'pulse-card' : ''}`}
                  style={{
                    minWidth: '240px',
                    borderColor: isSelected ? '#2563EB' : '#E2E8F0',
                    background: isSelected ? '#EFF6FF' : '#FFFFFF',
                    textAlign: 'left',
                    cursor: 'pointer'
                  }}
                >
                  <div className="badge badge-primary" style={{ marginBottom: '0.5rem' }}>
                    {alloc.classEntity.name}
                  </div>
                  <div style={{ fontWeight: 800, fontSize: '1rem', color: '#0F172A' }}>
                    {alloc.subject.code} - {alloc.subject.name}
                  </div>
                  <div style={{ fontSize: '0.78rem', color: '#64748B', marginTop: '0.35rem' }}>
                    Credits: {alloc.subject.credits} | Semester {alloc.classEntity.semester}
                  </div>
                </button>
              );
            })}
          </div>

          {selectedAllocation && (
            <div className="glass-card">
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1.5rem', flexWrap: 'wrap', gap: '1rem' }}>
                <div>
                  <h3 style={{ fontSize: '1.25rem', fontWeight: 800 }}>
                    Grade Sheet: {selectedAllocation.subject.name} ({selectedAllocation.classEntity.name})
                  </h3>
                  <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                    Total Enrolled Students: {students.length}
                  </span>
                </div>

                <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center' }}>
                  <div className="form-group" style={{ marginBottom: 0 }}>
                    <select className="form-select" value={markType} onChange={e => setMarkType(e.target.value)} style={{ padding: '0.5rem 1rem' }}>
                      <option value="INTERNAL_1">Internal Test 1</option>
                      <option value="INTERNAL_2">Internal Test 2</option>
                      <option value="ASSIGNMENT">Assignment</option>
                      <option value="SEMESTER">Final Semester Exam</option>
                    </select>
                  </div>
                  {markType !== 'SEMESTER' && (
                    <div className="form-group" style={{ marginBottom: 0, width: '100px' }}>
                      <input type="number" className="form-input" value={maxMarks} onChange={e => setMaxMarks(e.target.value)} title="Max Marks" placeholder="Max" style={{ padding: '0.5rem' }} />
                    </div>
                  )}
                </div>
              </div>

              {/* Student Marks Table */}
              <div className="table-container">
                <table className="custom-table">
                  <thead>
                    <tr>
                      <th>Register Number</th>
                      <th>Student Name</th>
                      <th>Current CGPA</th>
                      {markType === 'SEMESTER' ? (
                        <>
                          <th>Semester Score (100)</th>
                          <th>Action</th>
                        </>
                      ) : (
                        <>
                          <th>Score ({maxMarks})</th>
                          <th>Percentage %</th>
                          <th>Action</th>
                        </>
                      )}
                    </tr>
                  </thead>
                  <tbody>
                    {students.map(std => {
                      const currentVal = markType === 'SEMESTER' ? (semesterMarkData[std.id] || '') : (marksData[std.id] || '');
                      const numVal = parseFloat(currentVal);
                      const pct = !isNaN(numVal) && markType !== 'SEMESTER' ? (numVal / parseFloat(maxMarks)) * 100 : null;

                      return (
                        <tr key={std.id}>
                          <td style={{ fontWeight: 700, color: 'var(--primary)' }}>{std.registerNumber}</td>
                          <td style={{ fontWeight: 600 }}>{std.user.fullName}</td>
                          <td style={{ fontWeight: 700, color: std.cgpa < 6.0 ? '#fb7185' : '#34d399' }}>{std.cgpa ? std.cgpa.toFixed(2) : 'N/A'}</td>

                          {markType === 'SEMESTER' ? (
                            <>
                              <td>
                                <input
                                  type="number"
                                  className="form-input"
                                  placeholder="Exam Score"
                                  value={semesterMarkData[std.id] || ''}
                                  onChange={e => setSemesterMarkData({ ...semesterMarkData, [std.id]: e.target.value })}
                                  style={{ width: '120px', padding: '0.4rem 0.6rem' }}
                                />
                              </td>
                              <td>
                                <button onClick={() => handleSaveSemesterMark(std.id)} className="btn-primary btn-sm">
                                  <Save size={14} /> Submit Sem Mark
                                </button>
                              </td>
                            </>
                          ) : (
                            <>
                              <td>
                                <input
                                  type="number"
                                  className="form-input"
                                  placeholder="Marks"
                                  value={marksData[std.id] || ''}
                                  onChange={e => setMarksData({ ...marksData, [std.id]: e.target.value })}
                                  style={{ width: '110px', padding: '0.4rem 0.6rem' }}
                                />
                              </td>
                              <td>
                                {pct !== null ? (
                                  <span className={`badge ${pct >= 75 ? 'badge-success' : pct >= 50 ? 'badge-warning' : 'badge-danger'}`}>
                                    {pct.toFixed(1)}%
                                  </span>
                                ) : '-'}
                              </td>
                              <td>
                                <button onClick={() => handleSaveInternalMark(std.id)} className="btn-primary btn-sm">
                                  <Save size={14} /> Save
                                </button>
                              </td>
                            </>
                          )}
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
};
