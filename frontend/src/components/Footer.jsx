import { Mail, MapPin, Phone } from 'lucide-react';
import './Footer.css';

export default function Footer() {
    return (
        <footer className="footer-new">
            <div className="container">
                <div className="footer-top">
                    <h2>Need help?</h2>
                    <p className="footer-subtitle">Our customer service team will be happy to assist you.</p>
                </div>

                <div className="footer-contact-grid">
                    {/* Phone */}
                    <div className="contact-item">
                        <h4 className="contact-label">Phone No:</h4>
                        <div className="contact-details">
                            <p className="contact-text">+91 7010903976</p>
                            <p className="contact-text">+91 9597025035</p>
                            <p className="contact-text text-sm secondary">Mon - Fri / 12am to 12pm</p>
                        </div>
                        <div className="contact-icon-circle">
                            <Phone size={24} />
                        </div>
                    </div>

                    {/* Location */}
                    <div className="contact-item">
                        <h4 className="contact-label">Location</h4>
                        <div className="contact-details">
                            <p className="contact-text">Andavar Rice Mill, South Raja Street,</p>
                            <p className="contact-text">Kolathur - 636303</p>
                        </div>
                        <div className="contact-icon-circle">
                            <MapPin size={24} />
                        </div>
                    </div>

                    {/* Email */}
                    <div className="contact-item">
                        <h4 className="contact-label">Write Us</h4>
                        <div className="contact-details">
                            <p className="contact-text">andavarmillklr@gmail.com</p>
                        </div>
                        <div className="contact-icon-circle">
                            <Mail size={24} />
                        </div>
                    </div>
                </div>

                <div className="footer-bottom-inline">
                    <p className="copyright">Copyright © 2026 Andavar Rice Mill. All Rights Reserved</p>
                    <div className="footer-links-inline">
                        <a href="#">Terms of use</a>
                        <span className="separator">|</span>
                        <a href="#">Privacy Policy</a>
                    </div>
                </div>

                <a
                    href="https://wa.me/917010903976?text=I%20want%20to%20place%20order,%20Please%20help!"
                    className="whatsapp-float"
                    target="_blank"
                    rel="noopener noreferrer"
                    aria-label="Chat on WhatsApp"
                >
                    <img src="/images/wa.gif" alt="WhatsApp Chat" className="whatsapp-gif" />
                </a>
            </div>
        </footer>
    );
}
