import React, { useState, useEffect, useRef } from 'react';
import { apiService } from '../services/api';
import { Sparkles, Send, Bot, User, HelpCircle, Flame } from 'lucide-react';

export const AiAdvisorChat = () => {
  const [messages, setMessages] = useState([]);
  const [prompt, setPrompt] = useState('');
  const [sending, setSending] = useState(false);
  const chatEndRef = useRef(null);

  const quickPrompts = [
    "Check my CGPA target and advice",
    "How to improve low internal test marks?",
    "Certificate upload and academic credits guidance",
    "Provide a 30-day study strategy for Data Structures"
  ];

  const loadChatHistory = async () => {
    try {
      const history = await apiService.getAiHistory();
      setMessages(history);
    } catch (err) {
      console.error('Error loading AI history:', err);
    }
  };

  useEffect(() => {
    loadChatHistory();
  }, []);

  useEffect(() => {
    chatEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, sending]);

  const handleSendPrompt = async (textToSend) => {
    const query = textToSend || prompt;
    if (!query.trim() || sending) return;

    const tempUserMsg = { id: Date.now(), prompt: query, response: null, timestamp: new Date().toISOString() };
    setMessages(prev => [...prev, tempUserMsg]);
    setPrompt('');
    setSending(true);

    try {
      const aiReply = await apiService.askAi(query);
      setMessages(prev => prev.map(m => m.id === tempUserMsg.id ? aiReply : m));
    } catch (err) {
      console.error('AI chat error:', err);
    } finally {
      setSending(false);
    }
  };

  return (
    <div className="page-wrapper" style={{ display: 'flex', flexDirection: 'column', height: 'calc(100vh - 100px)' }}>
      <div style={{ marginBottom: '1.25rem' }}>
        <h2 className="page-title" style={{ display: 'flex', alignItems: 'center', gap: '0.65rem', color: '#0F172A' }}>
          <Sparkles color="#06B6D4" size={28} /> EduSphere AI Academic Intelligence Advisor
        </h2>
        <p className="page-subtitle">Personalized academic guidance, risk analysis, study planning, and target CGPA calculator</p>
      </div>

      {/* Suggestion Chips */}
      <div style={{ display: 'flex', gap: '0.6rem', flexWrap: 'wrap', marginBottom: '1.25rem' }}>
        {quickPrompts.map((qp, idx) => (
          <button
            key={idx}
            onClick={() => handleSendPrompt(qp)}
            className="btn-secondary btn-sm"
            style={{ borderRadius: '20px', background: '#EFF6FF', borderColor: '#BFDBFE', color: '#2563EB' }}
          >
            <Flame size={14} color="#06B6D4" /> {qp}
          </button>
        ))}
      </div>

      {/* Main Chat Container */}
      <div className="glass-card" style={{ flex: 1, display: 'flex', flexDirection: 'column', overflow: 'hidden', padding: 0, background: '#FFFFFF', border: '1px solid #E2E8F0' }}>
        {/* Messages Scroll Area */}
        <div style={{ flex: 1, overflowY: 'auto', padding: '1.5rem', display: 'flex', flexDirection: 'column', gap: '1.25rem', background: '#FAFAFA' }}>
          {messages.length === 0 && !sending && (
            <div style={{ textAlign: 'center', margin: 'auto', color: '#64748B' }}>
              <Bot size={48} color="#06B6D4" style={{ marginBottom: '1rem', opacity: 0.9 }} />
              <h3 style={{ color: '#0F172A', marginBottom: '0.5rem', fontWeight: 700 }}>Ask EduSphere AI Anything!</h3>
              <p style={{ fontSize: '0.9rem', maxWidth: '450px', margin: '0 auto', color: '#64748B' }}>
                Ask about your academic history, how to recover low grades, credit requirements, or custom study strategies.
              </p>
            </div>
          )}

          {messages.map(msg => (
            <React.Fragment key={msg.id || Math.random()}>
              {/* User Prompt */}
              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem' }}>
                <div style={{
                  background: 'linear-gradient(135deg, #2563EB 0%, #4F46E5 100%)',
                  color: '#ffffff',
                  padding: '0.85rem 1.25rem',
                  borderRadius: '18px 18px 2px 18px',
                  maxWidth: '75%',
                  fontSize: '0.92rem',
                  boxShadow: '0 4px 12px rgba(37, 99, 235, 0.2)'
                }}>
                  {msg.prompt}
                </div>
                <div style={{ width: '36px', height: '36px', borderRadius: '50%', background: '#DBEAFE', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <User size={18} color="#2563EB" />
                </div>
              </div>

              {/* AI Response */}
              {msg.response ? (
                <div style={{ display: 'flex', justifyContent: 'flex-start', gap: '0.75rem' }}>
                  <div style={{ width: '36px', height: '36px', borderRadius: '50%', background: 'linear-gradient(135deg, #06B6D4 0%, #2563EB 100%)', display: 'flex', alignItems: 'center', justifyContent: 'center', boxShadow: '0 4px 10px rgba(6, 182, 212, 0.3)' }}>
                    <Bot size={20} color="#ffffff" />
                  </div>
                  <div style={{
                    background: '#FFFFFF',
                    border: '1px solid #E2E8F0',
                    color: '#0F172A',
                    padding: '1rem 1.25rem',
                    borderRadius: '2px 18px 18px 18px',
                    maxWidth: '80%',
                    fontSize: '0.92rem',
                    lineHeight: '1.6',
                    whiteSpace: 'pre-line',
                    boxShadow: '0 2px 4px rgba(15, 23, 42, 0.04)'
                  }}>
                    {msg.response}
                  </div>
                </div>
              ) : (
                <div style={{ display: 'flex', justifyContent: 'flex-start', gap: '0.75rem' }}>
                  <div style={{ width: '36px', height: '36px', borderRadius: '50%', background: 'linear-gradient(135deg, #06B6D4 0%, #2563EB 100%)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <Bot size={20} color="#ffffff" />
                  </div>
                  <div style={{ background: '#FFFFFF', border: '1px solid #E2E8F0', padding: '0.85rem 1.25rem', borderRadius: '2px 18px 18px 18px', color: '#64748B', fontSize: '0.9rem' }}>
                    EduSphere AI is thinking...
                  </div>
                </div>
              )}
            </React.Fragment>
          ))}
          <div ref={chatEndRef} />
        </div>

        {/* Input Bar */}
        <div style={{ padding: '1rem', borderTop: '1px solid #E2E8F0', background: '#FFFFFF' }}>
          <form onSubmit={e => { e.preventDefault(); handleSendPrompt(); }} style={{ display: 'flex', gap: '0.75rem' }}>
            <input
              className="form-input"
              placeholder="Ask AI Academic Advisor..."
              value={prompt}
              onChange={e => setPrompt(e.target.value)}
              disabled={sending}
              style={{ borderRadius: '24px', paddingLeft: '1.25rem' }}
            />
            <button type="submit" className="btn-primary" disabled={sending} style={{ borderRadius: '24px', padding: '0.75rem 1.5rem' }}>
              <Send size={18} /> Send
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};
