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
import com.cinema.websocket.SeatEvent;
import com.cinema.websocket.SeatLockService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final MovieService movieService;
    private final UserService userService;
    private final QRCodeService qrCodeService;
    private final PricingContext pricingContext;
    private final SimpMessagingTemplate messagingTemplate;
    private final SeatLockService seatLockService;

    public BookingService(BookingRepository bookingRepository,
                          MovieService movieService,
                          UserService userService,
                          QRCodeService qrCodeService,
                          PricingContext pricingContext,
                          SimpMessagingTemplate messagingTemplate,
                          SeatLockService seatLockService) {
        this.bookingRepository = bookingRepository;
        this.movieService = movieService;
        this.userService = userService;
        this.qrCodeService = qrCodeService;
        this.pricingContext = pricingContext;
        this.messagingTemplate = messagingTemplate;
        this.seatLockService = seatLockService;
    }

    public List<Integer> getBookedSeatNumbers(Long movieId, LocalDateTime showTime) {
        List<Booking> bookings = bookingRepository.findByMovieIdAndShowTimeAndStatus(movieId, showTime, "CONFIRMED");
        List<Integer> booked = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.getSeatNumbers() != null && !b.getSeatNumbers().isEmpty()) {
                for (String s : b.getSeatNumbers().split(",")) {
                    booked.add(Integer.parseInt(s.trim()));
                }
            }
        }
        return booked;
    }

    public BookingDTO createBooking(String email, BookingRequestDTO request) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Movie movie = movieService.getMovieById(request.getMovieId());

        List<Integer> requestedSeats = request.getSeatNumbers();
        if (requestedSeats == null || requestedSeats.isEmpty()) {
            throw new BadRequestException("At least one seat must be selected");
        }

        if (request.getShowTime() == null) {
            throw new BadRequestException("Show time is required");
        }

        // Check for seat conflicts with existing CONFIRMED bookings for this specific showtime
        List<Integer> alreadyBooked = getBookedSeatNumbers(request.getMovieId(), request.getShowTime());
        List<Integer> conflicts = requestedSeats.stream()
                .filter(alreadyBooked::contains)
                .collect(Collectors.toList());

        if (!conflicts.isEmpty()) {
            throw new BadRequestException("Seats already booked: " + conflicts);
        }

        if (movie.getAvailableSeats() < requestedSeats.size()) {
            throw new BadRequestException("Not enough seats available");
        }

        movie.setAvailableSeats(movie.getAvailableSeats() - requestedSeats.size());
        movieService.updateMovieSeats(movie);

        double price = movie.getPrice() != null ? movie.getPrice() : 0.0;

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setMovie(movie);
        booking.setNumberOfSeats(requestedSeats.size());
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus("CONFIRMED");
        booking.setTotalPrice(pricingContext.calculatePrice(price, requestedSeats.size()));
        booking.setSeatNumbers(requestedSeats.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")));
        booking.setShowTime(request.getShowTime());
        booking.setVerificationToken(UUID.randomUUID().toString());

        Booking savedBooking = bookingRepository.save(booking);

        // Broadcast seat booked events via WebSocket
        for (Integer seat : requestedSeats) {
            SeatEvent bookedEvent = new SeatEvent(
                    movie.getId(), request.getShowTime().toString(), String.valueOf(seat),
                    SeatEvent.Action.BOOKED, null
            );
            messagingTemplate.convertAndSend(
                    "/topic/seats/" + movie.getId() + "/" + request.getShowTime(),
                    bookedEvent
            );
        }

        try {
            String qrCode = qrCodeService.generateQRCode(
                savedBooking.getId(),
                user.getName(),
                movie.getTitle(),
                requestedSeats.size()
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

    public BookingDTO verifyBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        return convertToDTO(booking);
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

        // Broadcast seat released events via WebSocket
        if (booking.getSeatNumbers() != null) {
            for (String s : booking.getSeatNumbers().split(",")) {
                SeatEvent releasedEvent = new SeatEvent(
                        movie.getId(), booking.getShowTime().toString(), s.trim(),
                        SeatEvent.Action.RELEASED, null
                );
                messagingTemplate.convertAndSend(
                        "/topic/seats/" + movie.getId() + "/" + booking.getShowTime(),
                        releasedEvent
                );
            }
        }
        booking.setSeatNumbers(null);
        bookingRepository.save(booking);
    }

    public void bulkDeleteBookings(List<Long> ids) {
        for (Long id : ids) {
            Booking booking = bookingRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
            
            // Restore movie seats before deletion
            if ("CONFIRMED".equals(booking.getStatus()) && booking.getMovie() != null) {
                Movie movie = booking.getMovie();
                movie.setAvailableSeats(movie.getAvailableSeats() + booking.getNumberOfSeats());
                movieService.updateMovieSeats(movie);
            }
            
            bookingRepository.deleteById(id);
        }
    }

    private BookingDTO convertToDTO(Booking booking) {
        List<Integer> seatNumbers = new ArrayList<>();
        if (booking.getSeatNumbers() != null && !booking.getSeatNumbers().isEmpty()) {
            for (String s : booking.getSeatNumbers().split(",")) {
                seatNumbers.add(Integer.parseInt(s.trim()));
            }
        }
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUser().getId());
        dto.setUserEmail(booking.getUser().getEmail());
        dto.setUserName(booking.getUser().getName());
        dto.setMovieId(booking.getMovie().getId());
        dto.setMovieTitle(booking.getMovie().getTitle());
        dto.setMoviePosterUrl(booking.getMovie().getPosterUrl());
        dto.setNumberOfSeats(booking.getNumberOfSeats());
        dto.setBookingDate(booking.getBookingDate());
        dto.setStatus(booking.getStatus());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setSeatNumbers(seatNumbers);
        dto.setShowTime(booking.getShowTime());
        dto.setVerificationToken(booking.getVerificationToken());
        dto.setQrCode(booking.getQrCode());
        return dto;
    }
}