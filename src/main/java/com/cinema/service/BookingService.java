package com.cinema.service;

import com.cinema.dto.BookingDTO;
import com.cinema.dto.BookingRequestDTO;
import com.cinema.model.Booking;
import com.cinema.model.Movie;
import com.cinema.model.User;
import com.cinema.pricing.PricingContext;
import com.cinema.repository.BookingRepository;
import com.cinema.exception.BadRequestException;
import com.cinema.exception.ResourceNotFoundException;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final MovieService movieService;
    private final UserService userService;
    private final QRCodeService qrCodeService;
    private final PricingContext pricingContext;

    public BookingService(BookingRepository bookingRepository,
                          MovieService movieService,
                          UserService userService,
                          QRCodeService qrCodeService,
                          PricingContext pricingContext) {
        this.bookingRepository = bookingRepository;
        this.movieService = movieService;
        this.userService = userService;
        this.qrCodeService = qrCodeService;
        this.pricingContext = pricingContext;
    }

    public BookingDTO createBooking(String email, BookingRequestDTO request) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Movie movie = movieService.getMovieById(request.getMovieId());

        if (movie.getAvailableSeats() < request.getSeats()) {
            throw new IllegalArgumentException("Not enough seats available");
        }

        movie.setAvailableSeats(movie.getAvailableSeats() - request.getSeats());
        movieService.updateMovieSeats(movie);

        double price = movie.getPrice() != null ? movie.getPrice() : 0.0;

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setMovie(movie);
        booking.setNumberOfSeats(request.getSeats());
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus("CONFIRMED");
        booking.setTotalPrice(pricingContext.calculatePrice(price, request.getSeats()));

        Booking savedBooking = bookingRepository.save(booking);

        try {
            String qrCode = qrCodeService.generateQRCode(
                savedBooking.getId(),
                user.getName(),
                movie.getTitle(),
                request.getSeats()
            );
            savedBooking.setQrCode(qrCode);
            bookingRepository.save(savedBooking);
        } catch (Exception e) {
            // QR generation failed, booking still valid
        }

        return convertToDTO(savedBooking);
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    public byte[] generateTicketPdf(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        User user = booking.getUser();
        Movie movie = booking.getMovie();

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD, Color.RED);
            Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 12);

            Paragraph title = new Paragraph("CINEMA TICKET", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            document.add(new Paragraph("Booking ID: " + booking.getId(), headerFont));
            document.add(new Paragraph("Movie: " + movie.getTitle(), normalFont));
            document.add(new Paragraph("User: " + user.getName(), normalFont));
            document.add(new Paragraph("Email: " + user.getEmail(), normalFont));
            document.add(new Paragraph("Seats: " + booking.getNumberOfSeats(), normalFont));
            document.add(new Paragraph("Show Time: " + (movie.getShowTime() != null ? movie.getShowTime().format(formatter) : "N/A"), normalFont));
            document.add(new Paragraph("Total Price: $" + booking.getTotalPrice(), normalFont));
            document.add(new Paragraph("Booking Date: " + (booking.getBookingDate() != null ? booking.getBookingDate().format(formatter) : "N/A"), normalFont));
            document.add(new Paragraph("Status: " + booking.getStatus(), normalFont));
            document.add(Chunk.NEWLINE);

            byte[] qrBytes = qrCodeService.generateQRCodeBytes(
                booking.getId(),
                user.getName(),
                movie.getTitle(),
                booking.getNumberOfSeats()
            );
            com.lowagie.text.Image qrImage = com.lowagie.text.Image.getInstance(qrBytes);
            qrImage.scaleToFit(150, 150);
            qrImage.setAlignment(Element.ALIGN_CENTER);
            document.add(qrImage);

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new BadRequestException("Failed to generate ticket PDF: " + e.getMessage());
        }
    }

    public List<BookingDTO> getUserBookings(String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return bookingRepository.findByUserIdWithDetails(user.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getUserBookingsByUserId(Long userId) {
        return bookingRepository.findByUserIdWithDetails(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAllWithDetails().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        booking.setStatus("CANCELLED");
        Movie movie = booking.getMovie();
        movie.setAvailableSeats(movie.getAvailableSeats() + booking.getNumberOfSeats());
        movieService.updateMovieSeats(movie);
        bookingRepository.save(booking);
    }

    private BookingDTO convertToDTO(Booking booking) {
        return new BookingDTO(
            booking.getId(),
            booking.getUser().getId(),
            booking.getUser().getEmail(),
            booking.getMovie().getId(),
            booking.getMovie().getTitle(),
            booking.getNumberOfSeats(),
            booking.getBookingDate(),
            booking.getStatus(),
            booking.getTotalPrice()
        );
    }
}