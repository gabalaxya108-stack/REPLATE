import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ThemeProvider } from './context/ThemeContext';
import { Navbar } from './components/layout/Navbar';
import { Footer } from './components/layout/Footer';
import { Home } from './pages/Home';
import { ExploreDonations } from './pages/ExploreDonations';
import { Login } from './pages/Login';
import { Register } from './pages/Register';
import { DonorDashboard } from './pages/DonorDashboard';
import { NgoDashboard } from './pages/NgoDashboard';
import { VolunteerDashboard } from './pages/VolunteerDashboard';
import { NotFound } from './pages/NotFound';

export const App: React.FC = () => {
  return (
    <Router>
      <ThemeProvider>
        <AuthProvider>
          <div className="flex flex-col min-h-screen bg-warm-bg text-ink-primary font-sans transition-colors duration-200">
            <Navbar />
            <main className="flex-1">
              <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/explore" element={<ExploreDonations />} />
                <Route path="/donor/donations" element={<DonorDashboard />} />
                <Route path="/ngo/requests" element={<NgoDashboard />} />
                <Route path="/volunteer/dashboard" element={<VolunteerDashboard />} />
                <Route path="/deliveries" element={<VolunteerDashboard />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="*" element={<NotFound />} />
              </Routes>
            </main>
            <Footer />
          </div>
        </AuthProvider>
      </ThemeProvider>
    </Router>
  );
};

export default App;
