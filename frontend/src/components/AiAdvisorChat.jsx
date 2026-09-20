import React, { useState, useEffect, useRef } from 'react';
import { apiService } from '../services/api';
import { Sparkles, Send, Bot, User, Flame, Code, Github, Award, BarChart2, Database, Table, BookOpen } from 'lucide-react';

/**
 * Render Markdown content (bold, code blocks, bullet points, tables) neatly in React.
 */
const FormattedMessage = ({ text }) => {
  if (!text) return null;

  // Split into blocks by double newlines or table/code block boundaries
  const lines = text.split('\n');
  const blocks = [];
  let currentBlock = [];
  let inCode = false;
  let codeLang = '';
  let codeLines = [];
  let inTable = false;
  let tableRows = [];

  const flushTable = () => {
    if (tableRows.length > 0) {
      blocks.push({ type: 'table', rows: [...tableRows] });
      tableRows = [];
      inTable = false;
    }
  };

  const flushCode = () => {
    if (codeLines.length > 0) {
      blocks.push({ type: 'code', lang: codeLang, content: codeLines.join('\n') });
      codeLines = [];
      codeLang = '';
      inCode = false;
    }
  };

  for (let line of lines) {
    // Code block toggle
    if (line.trim().startsWith('```')) {
      if (inTable) flushTable();
      if (inCode) {
        flushCode();
      } else {
        inCode = true;
        codeLang = line.trim().replace('```', '');
      }
      continue;
    }

    if (inCode) {
      codeLines.push(line);
      continue;
    }

    // Table line detect
    if (line.trim().startsWith('|') && line.trim().endsWith('|')) {
      // Ignore markdown table header delimiter line like | --- | --- |
      if (line.replace(/[\s\-\|:]/g, '').length === 0) {
        continue;
      }
      inTable = true;
      const cells = line.split('|').map(c => c.trim()).filter((_, idx, arr) => idx > 0 && idx < arr.length - 1);
      tableRows.push(cells);
      continue;
    } else {
      if (inTable) flushTable();
    }

    if (line.trim() === '') {
      if (currentBlock.length > 0) {
        blocks.push({ type: 'text', content: currentBlock.join('\n') });
        currentBlock = [];
      }
    } else {
      currentBlock.push(line);
    }
  }

  if (inCode) flushCode();
  if (inTable) flushTable();
  if (currentBlock.length > 0) {
    blocks.push({ type: 'text', content: currentBlock.join('\n') });
  }

  const renderInlineFormatted = (rawText) => {
    // Process bold **text** and inline code `code`
    const parts = [];
    let regex = /(\*\*[^*]+\*\*|`[^`]+`)/g;
    let match;
    let lastIndex = 0;

    while ((match = regex.exec(rawText)) !== null) {
      if (match.index > lastIndex) {
        parts.push(rawText.substring(lastIndex, match.index));
      }
      const token = match[0];
      if (token.startsWith('**') && token.endsWith('**')) {
        parts.push(<strong key={match.index} style={{ color: '#0F172A', fontWeight: 700 }}>{token.slice(2, -2)}</strong>);
      } else if (token.startsWith('`') && token.endsWith('`')) {
        parts.push(<code key={match.index} style={{ background: '#F1F5F9', color: '#2563EB', padding: '0.15rem 0.4rem', borderRadius: '4px', fontSize: '0.85em', fontFamily: 'monospace' }}>{token.slice(1, -1)}</code>);
      }
      lastIndex = regex.lastIndex;
    }

    if (lastIndex < rawText.length) {
      parts.push(rawText.substring(lastIndex));
    }

    return parts;
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
      {blocks.map((block, bIdx) => {
        if (block.type === 'code') {
          return (
            <div key={bIdx} style={{ background: '#0F172A', color: '#38BDF8', borderRadius: '8px', padding: '0.85rem 1rem', fontFamily: 'monospace', fontSize: '0.84rem', overflowX: 'auto', border: '1px solid #1E293B' }}>
              {block.lang && <div style={{ fontSize: '0.75rem', textTransform: 'uppercase', color: '#94A3B8', marginBottom: '0.5rem', borderBottom: '1px solid #334155', paddingBottom: '0.25rem' }}>{block.lang}</div>}
              <pre style={{ margin: 0, whiteSpace: 'pre-wrap' }}>{block.content}</pre>
            </div>
          );
        }

        if (block.type === 'table') {
          if (block.rows.length === 0) return null;
          const headerRow = block.rows[0];
          const bodyRows = block.rows.slice(1);

          return (
            <div key={bIdx} style={{ overflowX: 'auto', margin: '0.5rem 0', borderRadius: '8px', border: '1px solid #E2E8F0', boxShadow: '0 1px 3px rgba(0,0,0,0.05)' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.88rem', background: '#FFFFFF' }}>
                <thead>
                  <tr style={{ background: '#F8FAFC', borderBottom: '2px solid #E2E8F0' }}>
                    {headerRow.map((hCell, hIdx) => (
                      <th key={hIdx} style={{ padding: '0.6rem 0.85rem', textAlign: 'left', fontWeight: 600, color: '#334155' }}>
                        {renderInlineFormatted(hCell)}
                      </th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {bodyRows.map((rRow, rIdx) => (
                    <tr key={rIdx} style={{ borderBottom: '1px solid #F1F5F9', background: rIdx % 2 === 0 ? '#FFFFFF' : '#FAFAFA' }}>
                      {rRow.map((cCell, cIdx) => (
                        <td key={cIdx} style={{ padding: '0.55rem 0.85rem', color: '#1E293B' }}>
                          {renderInlineFormatted(cCell)}
                        </td>
                      ))}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          );
        }

        // Standard text block
        const blockLines = block.content.split('\n');
        return (
          <div key={bIdx}>
            {blockLines.map((line, lIdx) => {
              if (line.trim().startsWith('•') || line.trim().startsWith('-')) {
                return (
                  <div key={lIdx} style={{ display: 'flex', gap: '0.5rem', marginLeft: '0.5rem', marginBottom: '0.35rem' }}>
                    <span style={{ color: '#06B6D4' }}>•</span>
                    <div>{renderInlineFormatted(line.trim().substring(1).trim())}</div>
                  </div>
                );
              }
              if (line.trim().startsWith('#')) {
                const headerText = line.replace(/^#+\s*/, '');
                return <h4 key={lIdx} style={{ margin: '0.6rem 0 0.3rem 0', color: '#0F172A', fontWeight: 700, fontSize: '1rem' }}>{renderInlineFormatted(headerText)}</h4>;
              }
              return (
                <p key={lIdx} style={{ margin: '0 0 0.35rem 0' }}>
                  {renderInlineFormatted(line)}
                </p>
              );
            })}
          </div>
        );
      })}
    </div>
  );
};

export const AiAdvisorChat = () => {
  const [messages, setMessages] = useState([]);
  const [prompt, setPrompt] = useState('');
  const [sending, setSending] = useState(false);
  const [conversationId, setConversationId] = useState(null);
  const chatEndRef = useRef(null);

  const quickPrompts = [
    { label: "What are my internal marks?", icon: Table },
    { label: "What is my CGPA & semester GPA?", icon: Sparkles },
    { label: "Show all subjects and credits", icon: BookOpen },
    { label: "List my certificates & status", icon: Award },
    { label: "Who are the IT department faculty?", icon: Database },
    { label: "Compare my semester performance", icon: BarChart2 },
    { label: "How is my LeetCode progress?", icon: Code },
    { label: "Analyze my GitHub activity", icon: Github }
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
      const res = await apiService.askStudentAi(query, conversationId);
      if (res && res.conversationId) {
        setConversationId(res.conversationId);
      }
      const replyMessage = {
        id: Date.now() + 1,
        prompt: query,
        response: res.response || res.message || "Thank you for asking EduSphere AI.",
        timestamp: res.timestamp || new Date().toISOString()
      };

      setMessages(prev => prev.map(m => m.id === tempUserMsg.id ? replyMessage : m));
    } catch (err) {
      console.error('AI chat error:', err);
      const errorMsg = {
        id: Date.now() + 2,
        prompt: query,
        response: "I'm unable to access the AI service right now. Please try again shortly.",
        timestamp: new Date().toISOString()
      };
      setMessages(prev => prev.map(m => m.id === tempUserMsg.id ? errorMsg : m));
    } finally {
      setSending(false);
    }
  };

  return (
    <div className="page-wrapper" style={{ display: 'flex', flexDirection: 'column', height: 'calc(100vh - 100px)' }}>
      <div style={{ marginBottom: '1rem' }}>
        <h2 className="page-title" style={{ display: 'flex', alignItems: 'center', gap: '0.65rem', color: '#0F172A' }}>
          <Sparkles color="#06B6D4" size={28} /> EduSphere AI — Database & Intelligence Advisor
        </h2>
        <p className="page-subtitle">Real-time natural language SQL querying of your academic performance, database records, certificates, and coding metrics.</p>
      </div>

      {/* Suggestion Chips */}
      <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap', marginBottom: '1rem' }}>
        {quickPrompts.map((qp, idx) => {
          const IconComp = qp.icon;
          return (
            <button
              key={idx}
              onClick={() => handleSendPrompt(qp.label)}
              className="btn-secondary btn-sm"
              style={{ borderRadius: '20px', background: '#EFF6FF', borderColor: '#BFDBFE', color: '#2563EB', fontSize: '0.82rem', padding: '0.35rem 0.75rem', display: 'flex', alignItems: 'center', gap: '0.35rem' }}
            >
              <IconComp size={13} color="#06B6D4" /> {qp.label}
            </button>
          );
        })}
      </div>

      {/* Main Chat Container */}
      <div className="glass-card" style={{ flex: 1, display: 'flex', flexDirection: 'column', overflow: 'hidden', padding: 0, background: '#FFFFFF', border: '1px solid #E2E8F0' }}>
        {/* Messages Scroll Area */}
        <div style={{ flex: 1, overflowY: 'auto', padding: '1.25rem', display: 'flex', flexDirection: 'column', gap: '1.25rem', background: '#FAFAFA' }}>
          {messages.length === 0 && !sending && (
            <div style={{ textAlign: 'center', margin: 'auto', color: '#64748B' }}>
              <Bot size={48} color="#06B6D4" style={{ marginBottom: '1rem', opacity: 0.9 }} />
              <h3 style={{ color: '#0F172A', marginBottom: '0.5rem', fontWeight: 700 }}>Ask EduSphere AI Anything!</h3>
              <p style={{ fontSize: '0.9rem', maxWidth: '540px', margin: '0 auto', color: '#64748B' }}>
                Ask any question in natural language! The AI automatically converts your question into an SQL database query, fetches live records, and presents structured AI reports.
              </p>
            </div>
          )}

          {messages.map((msg, index) => (
            <React.Fragment key={msg.id || index}>
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
                <div style={{ width: '36px', height: '36px', borderRadius: '50%', background: '#DBEAFE', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                  <User size={18} color="#2563EB" />
                </div>
              </div>

              {/* AI Response */}
              {msg.response ? (
                <div style={{ display: 'flex', justifyContent: 'flex-start', gap: '0.75rem' }}>
                  <div style={{ width: '36px', height: '36px', borderRadius: '50%', background: 'linear-gradient(135deg, #06B6D4 0%, #2563EB 100%)', display: 'flex', alignItems: 'center', justifyContent: 'center', boxShadow: '0 4px 10px rgba(6, 182, 212, 0.3)', flexShrink: 0 }}>
                    <Bot size={20} color="#ffffff" />
                  </div>
                  <div style={{
                    background: '#FFFFFF',
                    border: '1px solid #E2E8F0',
                    color: '#0F172A',
                    padding: '1rem 1.25rem',
                    borderRadius: '2px 18px 18px 18px',
                    maxWidth: '85%',
                    fontSize: '0.92rem',
                    lineHeight: '1.6',
                    boxShadow: '0 2px 4px rgba(15, 23, 42, 0.04)'
                  }}>
                    <FormattedMessage text={msg.response} />
                  </div>
                </div>
              ) : (
                <div style={{ display: 'flex', justifyContent: 'flex-start', gap: '0.75rem' }}>
                  <div style={{ width: '36px', height: '36px', borderRadius: '50%', background: 'linear-gradient(135deg, #06B6D4 0%, #2563EB 100%)', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                    <Bot size={20} color="#ffffff" />
                  </div>
                  <div style={{ background: '#FFFFFF', border: '1px solid #E2E8F0', padding: '0.85rem 1.25rem', borderRadius: '2px 18px 18px 18px', color: '#64748B', fontSize: '0.9rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <Database size={16} className="animate-spin" color="#06B6D4" /> EduSphere AI is converting query to SQL & fetching database records...
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
              placeholder="Ask any natural language question to query the database (e.g. What are my marks in Java?)..."
              value={prompt}
              onChange={e => setPrompt(e.target.value)}
              disabled={sending}
              style={{ borderRadius: '24px', paddingLeft: '1.25rem' }}
            />
            <button type="submit" className="btn-primary" disabled={sending} style={{ borderRadius: '24px', padding: '0.75rem 1.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Send size={18} /> Send
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};
