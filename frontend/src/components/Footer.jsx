import '../styles/Footer.css';

const Footer = () => (
  <footer className="footer">
    <div className="footer-container">
      <div className="footer-brand">
        <span className="footer-icon">&#127909;</span>
        <span className="footer-name">CineReserve</span>
      </div>
      <p className="footer-copy">&copy; {new Date().getFullYear()} CineReserve. All rights reserved.</p>
      <div className="footer-links">
        <a href="#">Privacy</a>
        <a href="#">Terms</a>
        <a href="#">Support</a>
      </div>
    </div>
  </footer>
);

export default Footer;
