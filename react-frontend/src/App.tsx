import { Routes, Route, Navigate } from 'react-router-dom';
import LoginForm from './components/auth/LoginForm';
import RegisterForm from './components/auth/RegisterForm';
import ShelfList from './components/shelves/ShelfList';
import ShelfDetail from './components/shelves/ShelfDetail';
import ReviewList from './components/reviews/ReviewList';

function isAuthenticated() {
  return !!localStorage.getItem('token');
}

function PrivateRoute({ children }: { children: React.ReactNode }) {
  return isAuthenticated() ? <>{children}</> : <Navigate to="/login" replace />;
}

export default function App() {
  return (
    <Routes>
      <Route path="/login"    element={<LoginForm />} />
      <Route path="/register" element={<RegisterForm />} />

      <Route path="/shelves" element={
        <PrivateRoute><ShelfList /></PrivateRoute>
      } />
      <Route path="/shelves/:id" element={
        <PrivateRoute><ShelfDetail /></PrivateRoute>
      } />
      <Route path="/reviews/:bookId" element={<ReviewList />} />

      <Route path="*" element={<Navigate to="/shelves" replace />} />
    </Routes>
  );
}
